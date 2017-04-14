protoc --proto_path=. -IOTRServer/src/ --java_out=OTRServer/src/ messaging.proto
protoc --proto_path=. -IOTRMessenger/src/ --java_out=OTRMessenger/src/ messaging.proto
