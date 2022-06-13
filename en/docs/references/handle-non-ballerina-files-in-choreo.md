# Handle Non-Ballerina Files in Choreo
You can add and use non-Ballerina files such as .pem, .txt, etc. in the integration solution you are developing in Choreo. You can add these files to the web editor (Choreo low-code editor) or keep them in your GitHub repository. 

If you run and test this solution with non-Ballerina files in the web editor, you get the expected outcome as the files are available in the environment. However, at build time, Choreo only copies the `Cloud.toml` and the Ballerina target/jar files to the Docker container. The target/jar does not by default include the non-Ballerina files you add into Choreo. Therefore, **Choreo does not copy the non-Ballerina files to the Docker container unless explicitly mentioned in the `Cloud.toml` file**. This may lead to a **`no such file or directory`** error at runtime.

To avoid the error `no such file or directory` and to copy the non-Ballerina files to the Docker container, follow the guidelines below:

## Troubleshoot the `no such file or directory` error

- If you are using the Web Editor, commit and push the non-Ballerina files from the web editor to the GitHub repository. 
- Add the file reference to the `Cloud.toml`.
 
 For example, if you added a file `data.txt` at `/config/workspace/data/data.txt` add the following entry to the `Cloud.toml` file. 

 ```
 [[container.copy.files]]
 sourceFile="./data/data.txt"
 target="/home/ballerina/data/data.txt"
 ```
 
!!! note
    - **sourceFile**: the file's relative path to the repository root. (For example, `dir1/file1.json` and not `/config/workspace/dir1/file1.json`)
    - **target**: the target is where Choreo copies the artifacts in the Docker container. **The target must always have the path prefix /home/ballerina/**.
