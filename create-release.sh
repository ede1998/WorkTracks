#!/bin/bash

grade_file='app/build.gradle'
debug_build='app/build/outputs/apk/debug/app-debug.apk'
release_build='app/build/outputs/apk/release/app-release.apk'
github_endpoint='https://api.github.com/repos/ede1998/WorkTracks/releases'
# load user and token from script
source github-login

determine_new_version() {
	version=`grep "^\s*versionName" "$grade_file" | sed 's/^\s*versionName\s\+"\(.*\)"$/\1/'`;

	read -p "Current semantic version is '$version'. What should the new version be? " version

	if [ -z "$version" ]; then
		echo 'No version given. Stopping release creation.'
		exit 1;
	fi

	echo "$version"
}

update_version() {
	export new_version="$1"
	sed -i 's/^\(\s*versionCode\s\+\)\([0-9]\+\)$/echo "\1$((\2+1))"/e' "$grade_file"
	sed -i 's/^\(\s*versionName\s\+\).*$/echo "\1\\"$new_version\\""/e' "$grade_file"
	git add "$grade_file"
	git commit -m 'CHG: update version'
	git push
}

build_debug() {
	./gradlew assembleDebug
}
build_release() {
	./gradlew assembleRelease
}

tag_version() {
	tagname="v$1"
	git tag "$tagname"
	git push origin "$tagname"
}


extract_assets_url() {
	echo "$1" | python3 <(
cat << "EOF"
import json
import sys

input = json.load(sys.stdin)
print(input['upload_url'].split('{')[0])
EOF
	)
}

release_on_github() {
	echo 'Please type your description of the release (CTRL+D to finish)'
	description=`cat`
	input='{ "tag_name": "v'"$1"'", "name": "'"$1"'", "body": "'"$description"'", "draft": false, "prerelease": false }';

	response=`curl -s -u "$user:$token" -d "$input" -H "Content-Type: application/json" -X POST "$github_endpoint"`
}

publish() {
	assets_url="$1"
	file="$2"
	version="$3"
	name_postfix="$4"
	filename="work-tracks-$version-$name_postfix.apk"
	curl -s -u "$user:$token" -d "@$file" -H "Content-Type: application/vnd.android.package-archive" -X POST "$assets_url?name=$filename"
}


if [ `git status --porcelain | wc -l` != 0 ]; then
	git status
	echo ''
	read -p  'There are pending changes. Are you sure you want to continue? [Yes/No] ' yn
	case $yn in
		Yes|Y|y|yes)
			;;
		*)
			exit 2;
			;;
	esac
fi

new_version=`determine_new_version`
update_version "$new_version"
build_debug && build_release || exit 3;
tag_version "$new_version"
release_on_github "$new_version"
assets_url=`extract_assets_url "$response"`
echo "Asserturl = $assets_url"
publish "$assets_url" "$debug_build" "$new_version" "debug"
publish "$assets_url" "$release_build" "$new_version" "release"
