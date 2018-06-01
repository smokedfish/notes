# Building
Project is built using maven e.g.
```bash
% mvn clean install
```
A fat jar is built with a main class in the manifest.

# Running
*The following assumes a linux command line.*

**stdin**
```bash
% java -jar target/notes-1.0.0-RELEASE.jar --notesDir=/home/rob/notes2 --cacheDir=/home/rob/notes2/cache
```
Files in notesDir are rendered according to their extensions
* md : commonmark see [here](http://commonmark.org/)
* pu : plantuml see [here](http://plantuml.com/)

There are two possible rendering engines for the 'pu' extension: plantuml server or gravio - you change the code to select which.
