default:
	lein clean && lein garden once && lein cljsbuild once min

publish:
	aws s3 sync resources/public s3://perrygeo.test/daylight/ --delete --acl public-read
	echo "see http://perrygeo.test.s3.amazonaws.com/daylight/index.html"
