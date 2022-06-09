# Add Non-Ballerina files to Choreo

You can add and use non-Ballerina files such as .pem, .txt, etc. in the integration solution you are developing in Choreo. You can add these files to the web editor (Choreo low-code editor) or keep them in your GitHub repository. 

Running and testing this solution with non-Ballerina files in the web editor will give you results as expected as the files are available in the environment. However, at build time, Choreo only copies the `Cloud.toml` and the Ballerina target/jar files to the Docker container. The target/jar does not by default include the non-Ballerina files we add into Choreo. Therefore, **Choreo does not copy the non-Ballerina files to the Docker container unless explicitly mentioned in the `Cloud.toml` file**. This may lead to a **`no such file or directory`** error at runtime.

To avoid the error `no such file or directory` and to copy the non-Ballerina files to the Docker container, follow the guidelines below:

## Avoid the `no such file or directory` error

- If you are editing on the web editor, commit and push the non-Ballerina files from the web editor to the GitHub repository. 
- Add the file reference to the `Cloud.toml`.
 
 For example, if you added a file `data.txt` at `/config/workspace/data/data.txt` add the following entry to the `Cloud.toml` file. 

 ```
 [[container.copy.files]]
 sourceFile="./data/data.txt"
 target="/home/ballerina/data/data.txt"
 ```
 
!!! note
 - **sourceFile**: the file's relative path to the repository root. (For example, `dir1/file1.json` and not `/config/workspace/dir1/file1.json`)
 - **target**: The target is where Choreo copies the artifacts in the Docker container. **The target must always have the path prefix /home/ballerina/**.

# Add Non-Ballerina files to Choreo

You can add and use non-ballerina files such as .pem, .txt, etc. in the integration solution you are developing in Choreo. You can add these files to the web editor (Choreo low-code editor) or keep them in your GitHub repository. 

Running and testing this solution with non-ballerina files in the web editor will give you results as expected. However, when you deploy, internally, Choreo deploys this in a Docker container. In this process, Choreo is unable to copy the files to the Docker container without having any prior knowledge of the non-ballerina files used in the solution. This may lead to a `no such file or directory` error at runtime.

Internally Choreo only copies the `Cloud.toml` and the Ballerina target/jar files to the Docker container at build time. The target/jar does not include the non-ballerina files we add into Choreo. Choreo can copy these files to the Docker container at deployment by reading the `Cloud.toml` file. Hence we need to explicitly mention the files in the `Cloud.toml` file that Choroe needs to copy.

To avoid the error `no such file or directory` and to copy the non-Ballerina files to the Docker container, follow the guidelines below:

## Avoid the `no such file or directory` error

- If you are editing on the web editor, commit and push the non-Ballerina files from the web editor to the GitHub repository. 
- Add the file reference to the `Cloud.toml`.
 For example, if you added a file `data.txt` at `/config/workspace/data/data.txt` add the following entry to the Cloud.toml file. 

 ```
 [[container.copy.files]]
 sourceFile="./data/data.txt"
 target="/home/ballerina/data/data.txt"
 ```
 
 !!! note
 **sourceFile** : the file's relative path to the repository root. (For example, `dir1/file1.json` and not `/config/workspace/dir1/file1.json`)
**target**: the target must always have the path prefix /home/ballerina/. The target is where Choreo copies the artifacts inside the Docker container. 

