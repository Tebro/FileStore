# FileStore
A simple HTTP API for storing files.

[Download from releases.](https://github.com/Tebro/FileStore/releases)


## Usage

The system is configured via environment variables.

#### STORAGE_BACKEND

Defaults to "file" which uses the systems filesystem to store the data.

`export STORAGE_BACKEND=file`

Currently supported backends are:

 - file


#### STORAGE_PORT

Default: 10000

The TCP port that the server will listen on.

`export STORAGE_PORT=10000`


#### STORAGE_FILE_PATH

Only used with STORAGE_BACKEND=file 

Default: /tmp/fileStorage

The path on the filesystem where the data will be stored. 

`export STORAGE_FILE_PATH=/tmp/fileStorage`


### Running

After configuring the environment variables to your liking start the server with:

`java -jar /path/to/fileStore-all-0.1.jar`

There is also a Docker container available "tebro/filestore"

Can be used with the defaults:

`docker run -d -p 10000:10000 tebro/filestore`

or if you want to change some configuration (maybe want persistant storage?):

```
docker run -d -p 1337:1337 \
        -e STORAGE_PORT=1337 \
        -v /storage:/storage \
        -e STORAGE_FILE_PATH=/storage \
        tebro/filestore
```

### Using in your application

To store data with the system send it a HTTP POST request with the data you want to store in the request body.

The URL you use acts as the key. If you POST something to /foo/bar that will be the key.

To retrieve data from the system send it a HTTP GET request to a key that you have previously stored.

## Build

Build it with gradle

`./gradlew fatjar`

This will create a all-in-one .jar file in build/libs/