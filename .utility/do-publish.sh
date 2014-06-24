#!/bin/bash
if [[ "$TRAVIS_REPO_SLUG" != "MilkBowl/Vault" ]]
then
        echo 'Travis can only publish docs for release builds.'
        return 0
fi
if [[ "$TRAVIS_PULL_REQUEST" == "true" || "$TRAVIS_BRANCH" != "master" ]]
then
	mvn test
else
	mvn clean deploy --settings .utility/settings.xml
fi

exit $?