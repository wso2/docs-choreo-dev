//Create apis collection in apim MongoDB

var collectionExists = db.getCollectionNames().includes("apis");

if (!collectionExists) {
  db.createCollection("apis",{});
}
