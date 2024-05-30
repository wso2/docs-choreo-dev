//Create resources collection in resource registry MongoDB
var collectionExists = db.getCollectionNames().includes("resources");

if (!collectionExists) {
  db.createCollection("resources",{});
}

//Create indexes in resources collection in resource registry MongoDB
db.resources.createIndex({"idl.content": "text", "name": "text","description": "text", "summary": "text"}, {}, null);
db.resources.createIndex({serviceId : 1}, {unique: true});
