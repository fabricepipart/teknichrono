if [ -z "$NEW_TAG" ]
then
    VERSION_BASE=$(git describe --tags `git rev-list --tags --max-count=1`)
    export version="$VERSION_BASE-${GITHUB_SHA:0:8}"
    mvn -B -U versions:set -DnewVersion=${version} &> /dev/null
else
    export version="$NEW_TAG"
fi

echo -n $version