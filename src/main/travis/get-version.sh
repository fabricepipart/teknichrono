if [ $TRAVIS_PULL_REQUEST = "false" ] && [ $TRAVIS_BRANCH = 'master' ]; then export version="$VERSION_BASE.$TRAVIS_BUILD_NUMBER"; else export version="$VERSION_BASE.0-${TRAVIS_COMMIT:0:8}.$TRAVIS_BUILD_NUMBER"; fi
mvn -B -U versions:set -DnewVersion=${version} &> /dev/null
echo -n $version