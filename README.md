## CICD
The project is built and deplyed using Github Actions

To be able to get the projects changed since the last stable point not only was needed to create a tag but also the following step has the task of getting all the tags of the repo or polylith will never detect that there were changes
    - name: Get all git tags
        run: |
          git fetch --prune --unshallow --tags
          echo $?
          git tag --list

After a successfull build and deploy we should mark the commit as a stable point (in time). For that we use tags.
Commands to work with tags
git describe --abbrev=0 --tags
git tag -f -a "stable-0" -m "Message"
git push -u --tags --force

It its necessary to convert the result of the execution of the command below to Json so we can use it in the next step dinamically.
clojure -M:poly ws get:changes:changed-or-affected-projects since:stable skip:dev

For that babashka was included in the action and a script writen in clojure which is located in the 'scripts' folder on the root of the project.

## Build and run locally for testing
This command is going to build an uberjar to later contruct a docker image with.
clojure -T:build uberjar :project "project-name"

To build the docker image locally (inside the project to test). 
clojure -T:jib jibbit.core/build
It's also necessary to change the jib.edn ex:
:target-image {:image-name "repository/image"
                :type :docker}

In the project folder inside the "docker" folder run
docker-compose up

## Database in firebase
Commands
If there is nothig
firebase init
To run the emulator locally and test
firebase emulators:start --project volleyball-3-0

Where do i find the value for GOOGLE_APPLICATION_CREDENTIALS
The GOOGLE_APPLICATION_CREDENTIALS environment variable is typically set to the path of a JSON file that contains the service account key for your Google Cloud project. This JSON file is used to authenticate your application when making requests to Google Cloud services.

To find the value for GOOGLE_APPLICATION_CREDENTIALS, you will need to generate and download the JSON key file for your service account in the Google Cloud console. Here's how you can do it:

Open the Google Cloud Console (console.cloud.google.com) and select your project.

In the sidebar, navigate to "IAM & admin" > "Service accounts".

Locate the service account for which you want to generate a key and click on it.

Under the "Keys" tab, click on the "Add Key" button and select "Create new key".

Choose the key type as JSON and click on the "Create" button. This will generate and download the JSON key file to your local machine.

Once the JSON key file is downloaded, find its location on your computer. This location will be the value for GOOGLE_APPLICATION_CREDENTIALS.

Remember to keep the JSON key file secure and do not share it publicly, as it contains sensitive information that grants access to your Google Cloud resources.