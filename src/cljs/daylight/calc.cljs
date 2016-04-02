(ns daylight.calc)

(defn radians
  "Convert degrees to radians"
  [degrees]
  (* (/ (.-PI js/Math) 180) degrees))

(defn degrees
  "Convert radians to degrees"
  [radians]
  (* (/ 180 (.-PI js/Math)) radians))

(defn arccos-domain [x]
  (cond
    (<= x -1.0) (.-PI js/Math)
    (>= x 1.0) 0
    :else (.acos js/Math x)))

(defn daylight
  "Find hours of daylight given day of year and latitude in degrees.
  Formula from http://mathforum.org/library/drmath/view/56478.html"
  [lat-degrees day]
  (let [lat (radians lat-degrees)
        part1 (* 0.9671396 (.tan js/Math (* 0.00860 (- day 186))))
        part2 (.cos js/Math (+ 0.2163108 (* 2 (.atan js/Math part1))))
        p (.asin js/Math (* 0.39795 part2))
        numerator (+ (.sin js/Math 0.01454) (* (.sin js/Math lat) (.sin js/Math p)))
        denominator (* (.cos js/Math lat) (.cos js/Math p))
        arccos (arccos-domain (/ numerator denominator))]
    (- 24 (* 7.63944 arccos))))

(defn calculate-sun-hours [lat]
  (map (partial daylight lat) (range 0 365)))


;----------------------------------------------------------
; http://williams.best.vwh.net/sunrise_sunset_algorithm.htm
; All inputs and outputs in degrees
;----------------------------------------------------------

(defn sin [x]
  (.sin js/Math (radians x)))

(defn cos [x]
  (.cos js/Math (radians x)))

(defn tan [x]
  (.tan js/Math (radians x)))

(defn asin [x]
  (degrees (.asin js/Math x)))

(defn acos [x]
  (degrees (.acos js/Math x)))

(defn atan [x]
  (degrees (.atan js/Math x)))


(defn adjust-lon-domain [lon]
  (cond
    (<= lon 0) (adjust-lon-domain (+ lon 360))
    (> lon 360) (adjust-lon-domain (- lon 360))
    :else lon))


(defn approx-sun-time [lon day rise-set]
  (let [lng-hour (/ (adjust-lon-domain lon) 15)]
    (cond
      (= rise-set :rise) (+ day (/ (- 6 lng-hour) 24))
      (= rise-set :set) (+ day (/ (- 18 lng-hour) 24)))))


(defn sun-mean-anomaly [t]
  (- (* t 0.9856) 3.289))


(defn sun-true-lon [M]
  (let [calc-lon (+ M
                   (* 1.916 (sin M))
                   (* 0.020 (sin (* 2 M)))
                   282.684)]
    (adjust-lon-domain calc-lon)))


(defn right-ascension [L]
  (let [RA (adjust-lon-domain (atan (* 0.91764 (tan L))))
        leftquad (* (.floor js/Math (/ L 90)) 90)
        rightquad (* (.floor js/Math (/ RA 90)) 90)]
    (/ (+ RA (- leftquad rightquad)) 15)))


(defn sun-declination [L]
  (let [sin-dec (* 0.39782 (sin L))
        cos-dec (cos (asin sin-dec))]
    {:sindec sin-dec
     :cosdec cos-dec}))


(def zenith
  {:official 90.83333
   :civil 96.0
   :nautical 102.0
   :astronomical 108.0})


(defn sun-local-angle [zenith lat declination]
  (let [cosdec (:cosdec declination)
        sindec (:sindec declination)
        cosh (/ (- (cos zenith) (* sindec (sin lat)))
                (* cosdec (cos lat)))]
    ; gt 1 = sun never rises
    ; lt -1 = sun never sets
    (cond
      (> cosh 1) 1
      (< cosh -1) -1
      :else cosh)
    ))


(defn local-mean-time [H RA t]
  (- (+ H RA) (* 0.06571 t) 6.622))


(defn time-24hr [t]
  (cond
    (< t 0) (time-24hr (+ t 24))
    (> t 24) (time-24hr (- t 24))
    :else t))


(defn the-hour [cosh rise-set]
  (cond
    (= rise-set :rise) (/ (- 360 (acos cosh)) 15)
    (= rise-set :set) (/ (acos cosh) 15)))


(defn utc-offset
  "Determine utc-offset from location
  TODO use https://github.com/pelias/polygon-lookup "
  [lon lat]
  (.round js/Math (/ lon 15)))


(defn local-event [lon lat event zenith day]
  (let [t (approx-sun-time lon day event)
        M (sun-mean-anomaly t)
        L (sun-true-lon M)
        RA (right-ascension L)
        cosh (sun-local-angle zenith lat (sun-declination L))
        H (the-hour cosh event)
        T (local-mean-time H RA t)
        offset (utc-offset lon lat) ;; TODO get from lat lon U timezones
        UTC (- T (/ lon 15))]
    (cond
      (> cosh 0.999) 12
      (and (= event :rise) (< cosh -0.98)) 0
      (and (= event :set) (< cosh -0.98)) 24
      :default (time-24hr (+ UTC offset)))))


(defn calculate-sunrises [lon lat zenith]
  (map (partial local-event lon lat :rise zenith) (range 0 365)))


(defn calculate-sunsets [lon lat zenith]
  (map (partial local-event lon lat :set zenith) (range 0 365)))
