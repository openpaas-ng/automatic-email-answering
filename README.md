# Automatic-Email-Answering

Java Maven Project of the automatic-email-answering.

The automatic email answering is a rest-api application that predict for a meeting email writtern in french a best n (1 to 3) answers for this email.
The program is based on an ontology that describe meeting intents and answering canvas.
The ontology is available on the file intent6.owl. To show the conent of the ontology we suggest to open the file with Protégé OWL (https://protege.stanford.edu/)

# Running the service with Docker

## Building the image
```
wget https://raw.githubusercontent.com/openpaas-ng/automatic-email-answering/master/Dockerfile

docker build -t demo:smartreply .
```
## Running the image
**The image expose the port 9991 with the IP 0.0.0.0 .**  To run the service on a host port do the next instruction:
```
docker run -it -p 5551:9991 demo:smartreply
```
## Calling the service 
```
curl -X POST http://0.0.0.0:5551/rest/detectintent/parsejson -F "file=@/path/to/email.json"
```
A sample of an email.json content
```
 {
  
  "users":["zsellami@linagora.com"],
  "textBody": "Notre prochaine réunion se tiendra dans les locaux de linagora. Pour vous déplacer voici l'adresse:\n Tour Franklin, 100 Terrasse Boieldieu, 92800 Puteaux ",
  "subject": [
    "Linagora Réunion"
  ],
  
  "from": [
    {
      "name": "Dupont Dupont",
      "address": "dupont.dupont@dupont.fr"
    }
  ],
  "recipients": {
    "cc": [],
    "bcc": [],
    "to": [
      {
        "name": "Zied Sellami",
        "address": "zsellami@linagora.com"
      }
    ]
  }
}
```
Expected result:
```
[ {  
"label" : "Demander plus de détails sur le rendez-vous",  
"email" : "Bonjour Dupont Dupont,\n\nPouvez-vous me donner plus de détails sur la réunion?\n\nCordialement,\nZied Sellami",  
"score" : 10.798924731182796  
}, {  
"label" : "Merci pour ces précisions",  
"email" : "Bonjour Dupont Dupont,\n\nMerci pour ces précisions.\n\nCordialement,\nZied Sellami",  
"score" : 10.798924731182796  
} ]
```

## Service statistics
Use the next curl command to show how many times per minute the service is used:
```
curl -X GET http://0.0.0.0:5551/rest/detectintent/log
```

# Dependencies

*You need to install and run :*

**Duckling Facebook** : https://github.com/facebook/duckling

*You need to unzip Talismane NLP tools:*

**Talismane (server mode)** : https://github.com/joliciel-informatique/talismane/. The automatic-email-answering git already contain a modified version of the talismane tool. **We suggest to use this tuned version.**

# How To run the projet

## Building the project

To generate jar and lib dependencies. Please run :
```
mvn install -Dmaven.test.skip=true
```
A jar named **intentDetection-1.0.jar** and a lib directory will be generated at the target directory of the project.

## Config the application

Before running the application you need to:

 1. **Create a directory for the application** : ```mkdir application```
 2. **Copy the next files on the application directory** : ``` intentDection-1.0.jar, lib/, intent6.owl, firstname_female.lst, firstname_male.lst, TextCleaner.regex and talismane-distribution-5.1.2-bin, CONFIG```
 3. **Create a tmp directory**: ```mkdir application/tmp```
 4. **Configurate the different parameters on the CONFIG file**:
 DUCKLING_URL = http://127.0.0.1:8000/parse
DUCKLING_DIRECTORY = path\to\duckling\directory
ONTOLOGY_PATH = path\to\the\intent6.owl
SERVICE_URL = http://localhost/rest/
SERVICE_PORT = 9991
TMP_DIRECTORY = path\to\the\tmp\application\directory
TALISMANE_DIR = path\to\talismane\directory
TALISMANE_HOST_NAME = localhost
TALISMANE_PORT_NUMBER = 7272
TALISMANE_JAR_NAME = talismane-core-5.1.2.jar
TALISMANE_CONF_NAME = talismane-fr-5.0.4.conf

## Running the application

```
java -jar -Xmx4G -Xms4G /path/to/application/intentdetection.jar /path/to/application/CONFIG
```

## Using the api-rest

 1. Create a json email file **email.json**. This is an example:
 ```
 {
  
  "users":["zsellami@linagora.com"],
  "textBody": "Notre prochaine réunion se tiendra dans les locaux de linagora. Pour vous déplacer voici l'adresse:\n Tour Franklin, 100 Terrasse Boieldieu, 92800 Puteaux ",
  "subject": [
    "Linagora Réunion"
  ],
  
  "from": [
    {
      "name": "Dupont Dupont",
      "address": "dupont.dupont@dupont.fr"
    }
  ],
  "recipients": {
    "cc": [],
    "bcc": [],
    "to": [
      {
        "name": "Zied Sellami",
        "address": "zsellami@linagora.com"
      }
    ]
  }
}

 ```

 2. Calling the service
 ```
curl -X POST http://localhost:9991/rest/detectintent/parsejson -F "file=@/path/to/email.json"
 ```

 3. The result
 ```
[ {  
"label" : "Demander plus de détails sur le rendez-vous",  
"email" : "Bonjour Dupont Dupont,\n\nPouvez-vous me donner plus de détails sur la réunion?\n\nCordialement,\nZied Sellami",  
"score" : 10.798924731182796  
}, {  
"label" : "Merci pour ces précisions",  
"email" : "Bonjour Dupont Dupont,\n\nMerci pour ces précisions.\n\nCordialement,\nZied Sellami",  
"score" : 10.798924731182796  
} ]
 ```
