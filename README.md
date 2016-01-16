# FileStore
A simple HTTP API for storing files.

[Download from releases.](https://github.com/Tebro/FileStore/releases)


## Usage

The system is configured via environment variables.

#### STORAGE_BACKEND

Defaults to "memory" which stores the data in RAM.

`export STORAGE_BACKEND=file`

Currently supported backends are:

 - memory
 - file -- Stores the data on disk.


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

`java -jar /path/to/fileStore-all-$VERSION.jar`

There is also a Docker container available "tebro/filestore"

Can be used with the defaults:

`docker run -d -p 10000:10000 tebro/filestore`

or if you want to change some configuration (maybe want persistant storage?):

```
docker run -d -p 1337:1337 \
        -e STORAGE_PORT=1337 \
        -e STORAGE_BACKEND=file \
        -v /storage:/storage \
        -e STORAGE_FILE_PATH=/storage \
        tebro/filestore
```

## Using in your application

To store data with the system send it a HTTP POST request with the data you want to store in the request body.

The URL you use acts as the key. If you POST something to /foo/bar that will be the key.

To retrieve data from the system send it a HTTP GET request to a key that you have previously stored.

### HTTP Headers

Using the headers desrcibed below will affect the system in some way.

#### Content-Type

The standard HTTP Content-Type header is supported. It is stored as meta-data for your content and will be returned when
said data is accessed.

#### File-Store-TTL

This is a custom header implemented in this project. Add this header with a value in seconds to your POST request when
storing data, and the system will delete the object after the amount of seconds provided has passed. 

All of the current storage backends support this header.

## Build

Build it with gradle

`./gradlew fatjar`

This will create a all-in-one .jar file in build/libs/