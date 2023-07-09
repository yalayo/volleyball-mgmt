## CICD
The project is built and deplyed using Github Actions

To be able to get the projects changed since the last stable point not only was needed to create a tag but also the following step has the task of getting all the tags of the repo or polylith will never detect that there were changes
    - name: Get all git tags
        run: |
          git fetch --prune --unshallow --tags
          echo $?
          git tag --list

It its necessary to convert the result of the execution of the command below to Json so we can use it in the next step dinamically.
clojure -M:poly ws get:changes:changed-or-affected-projects since:stable skip:dev

For that babashka was included in the action and a script writen in clojure which is located in the 'scripts' folder on the root of the project.