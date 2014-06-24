#!/bin/bash
if [[ "$TRAVIS_REPO_SLUG" != "MilkBowl/Vault" || "$TRAVIS_PULL_REQUEST" == "true" || "$TRAVIS_BRANCH" != "master" ]]
then
        echo 'Travis can only publish docs for release builds.'
        return 0
fi

mvn clean deploy --settings .utility/settings.xml
