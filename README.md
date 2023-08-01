## Caloricam Backend

Caloricam was my MSc IT dissertation project. 
The goal of this project was to use Reverse Image Search to identify common foods and food products.
For example: When you eat at McDonalds, you could take a picture of the Big Yellow M and, theoretically, the app would show you items from McDonalds Menu to select from.
You could then pick the item you're eating from the menu and get the nutritional information.

There are 4 endpoints involved:

| Endpoint  | Path                                                                     | Description                                                                                                          |
|-----------|--------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| Upload    | POST /CalorieApp-1.0-SNAPSHOT/api/upload                                 | Uploads and indexes a picture. This should be a picture of food.                                                     |
| Link      | GET  /CalorieApp-1.0-SNAPSHOT/api/link?image_name=X.jpg&food_name=banana | Links an uploaded picture with a food name.                                                                          |
| Identify  | GET  /CalorieApp-1.0-SNAPSHOT/api/identify?image_name=Y.jpg              | Does a reverse image search on Y.jpg to find matching uploaded images. Returns the food name of similar images.      |
| Recognize | GET  /CalorieApp-1.0-SNAPSHOT/api/recognize?image_name=Y.jpg             | Does a reverse image search on Y.jpg to find matching uploaded images. Returns the nutrition info of similar images. |

I have cleaned up the project because I wanted to see it work (and show my kids) but there are a lot of limitations and technical problems:
- You could upload a picture of a puppy and claim it's a octopus. 
- Not a proper REST API
- No authentication

I won't be spending more time to build a proper application. Just enough to be able to run it as-is when I need to. 

## Troubleshooting

### Database Mutations aren't executing
I've migrated the project to Hibernate but did a rush job of it. You might need to begin and commit a transaction. See `ImageDAO` for an example.

### DAO method throws `NullPointerException` but none of the variables are `null`.
The `NullPointerException` might occur when the JNDI lookup is able to find the datasource but unable to use it to create a connection.
This usually means there's something wrong with the database e.g. the database failed to create or the docker container has shut down.
- Investigate the logs of the db container. 
- Delete the `docker/data` directory and restart the docker container `make up`

## How do you add a jar to a local maven repository
This project uses a local maven repository `.local-maven-repo` to store the `LIRe` library jar.
To add a new jar to this local maven repo, you can use the following command:

```
mvn deploy:deploy-file -DgroupId=net.semanticmetadata.lire -DartifactId=lire -Dversion=1.0b4 -Durl=file:./local-maven-repo/ -DrepositoryId=local-maven-repo -DupdateReleaseInfo=true -Dfile=/path/to/jar/example/lire.jar
```

## Useful Resources

- [LIRe Docuemntation](http://www.semanticmetadata.net/wiki/searchindex/)
- [LIRe Releases](http://www.itec.uni-klu.ac.at/~mlux/lire-release/)
- [Hibernate Tomcat JNDI DataSource Example Tutorial](https://www.digitalocean.com/community/tutorials/hibernate-tomcat-jndi-datasource-example-tutorial)
- [Can I inject an JPA EntityManager using CDI and @PersistenceContext, like with Spring?](https://stackoverflow.com/a/40479773)