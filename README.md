# Choreo Documentation

This is the Choreo documentation repository. This repository is open and we welcome your contributions!

To see the Choreo documentation site, go to [https://wso2.com/choreo/docs/](https://wso2.com/choreo/docs/)

## Contribute to Choreo documentation

Before you contribute, read the following guidelines to understand how you can start contributing:

1. Accept the contributor license agreement (CLA)

    You need to accept the contributor license agreement (CLA) when you are prompted via a GitHub email notification on sending your first pull request (PR). Subsequent PRs will not require CLA acceptance.

    If the CLA changes for some (unlikely) reason, you'll have to accept the new CLA text when you send your first PR after the change.

2. Fork this repository, make your changes, and send in a pull request.

   For an overview of the general voice, tone, content style, and text formatting to follow when contributing to Choreo documentation, see [Choreo Documentation Guidelines and Best Practices](https://github.com/wso2/docs-choreo-dev/wiki/Choreo-Documentation-Guidelines-and-Best-Practices)

We look forward to your contributions.


## Run the project locally with MkDocs

### Prerequisites
- Python 3
     - Setup guides: [MacOS](https://docs.python.org/3/using/mac.html), [Windows](https://docs.python.org/3/using/windows.html), [Linux](https://wiki.python.org/moin/BeginnersGuide/Download#:~:text=using/mac.html-,Linux,-On%20most%20Linux)
-  Pip3
      - Once you install Python, Pip is most likely to be installed by default. If not, follow the [Pip installation guide](https://pip.pypa.io/en/stable/installation/) for instructions.

### Setting up the project

#### Step 1: Clone the project
```shell
git clone https://github.com/[git-username]/docs-choreo-dev.git
```

#### Step 2: Create a Virtual Environment
- Navigate to the project's root where the `requirements.txt` file is located at.
```shell
cd docs-choreo-dev/en
```
- Execute the following commands to create a virtual environment and activate it.
```python
python3 -m venv dev-env
source dev-env/bin/activate
```

#### Step 3: Install dependancies
Execute the following command from the directory where the `requirements.txt` file is located at.
```python
pip3 install -r requirements.txt   
``` 

#### Step 4: Navigate to the project directory
Choreo documentation provides two perspectives: Developer and Platform Engineer. Each perspective is hosted on a separate server. To access the desired perspective, navigate to the corresponding project root.
- For Developer perspective:
```shell
cd en/developer-docs
```
- For Platform Engineer perspective:
```shell
cd en/pe-docs
```

#### Step 5: Start the development server
```shell
mkdocs serve --dirtyreload
```
Note: `--dirtyreload` flag is used to enable live reloading of the documentation site when changes are made to the source files. If you don't want to enable live reloading, you can remove this flag.
Choreo docs will be available locally on [http://localhost:8000/](http://localhost:8000/)

### Proof Reading Documentation

Prerequisites:
- [MdSpell](https://github.com/mtuchowski/mdspell)


#### Steps
1. Navigate to the `en` directory and run the following command:
    ```shell
    ./serve.sh
    ```
This script will check spellings and incorrect links. 

Incorrect spellings will be highlighted in red. You can take one of the following resolution paths:
- Choose the suggested correct spelling. 
- Proceed with "Ignore" to skip the word and not be prompted about it again during the current run. However, it will reappear if you rerun the command.
- Proceed with "Add to file ignores" to ignore the word in the current file.
- Proceed with "Add to dictionary - case insensitive" to add the word to the dictionary for all files, allowing it to match regardless of case. For example, with the word "Microsoft," both "Microsoft" and "Microsoft" would be considered correct.
- Proceed with "Add to dictionary - case sensitive" to add the word to the dictionary for all files, but only match the exact case used. For instance, with the word "Microsoft," the word "microsoft" would not be considered correct.

All exclusions will be stored in a .spelling file in the directory from which you run the command.
Choose resolutions consciously.

## Run the project locally with Dev Containers

This repository supports the VS Code dev containers feature, which allows you to create a consistent and isolated development environment inside a Docker container. To use this feature, you need to have the following pre requisites:

- VS Code
- Docker installed on your system
- [Remote - Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers) extension for VS Code

Once you have these installed, you can open the repository in VS Code and follow these steps:

- Press F1 and select the Remote-Containers: Open Folder in Container... command.
- Select the repository folder and wait for the container to build and start.
- You can now edit, run, debug, and test your code inside the container.

For more information on how to use VS Code dev containers, please refer to the official documentation: https://code.visualstudio.com/docs/remote/containers

## License

Licenses this source under the Apache License, Version 2.0 ([LICENSE](LICENSE)), You may not use this file except in compliance with the License.
