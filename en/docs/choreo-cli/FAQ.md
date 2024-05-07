# Frequently Asked Questions

## How do I uninstall the cli?
Unless you downloaded the binary directly, you can uninstall the CLI by deleting the `.choreo` directory in your 
home directory of your operating system.

## How do I update the cli?
You can update the CLI by running the following command:
```sh
curl -o- https://cli.choreo.dev/install.sh | bash
```

## What are the supported component types?
The Choreo CLI currently supports the following component types:
- Service
- Web Application
- Webhook
- Scheduled Task
- Manual Task

## How do I get help with a specific command?
You can get help with a specific command by running the following command:
```sh
choreo <command> --help
```

## What are the build configurations required when creation of components?
You can configure the component build configurations based on the component type as follows:

```sh
choreo create component <name> --project <name> --build-configs='key1=value1,key2=value2'
choreo create component <name> --project <name> --build-configs='key1=value1' --build-configs='key2=value2'
```

The build configurations required for existing buildpacks are as follows:

<table>
   <thead>
      <tr>
         <th>Component Type</th>
         <th>Buildpack</th>
         <th>Required Configurations</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <td rowspan=10>Service</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>n/a</td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>n/a</td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            <ul>
         </td>
      </tr>
      <tr>
         <td rowspan=11>Webapp</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
               <li>port: Port</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Static website</td>
         <td>n/a</td>
      </tr>
      <tr>
         <td>React</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Angular</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Vue</td>
         <td>
            <ul>
               <li>buildCommand: Command to be used for building the component</li>
               <li>outputDirectory: Output directory for the component build artifacts</li>
               <li>nodeVersion: Node.js version used</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td rowspan=9>Webhook</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            n/a
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            n/a
         </td>
      </tr>
      <tr>
         <td rowspan=10>Scheduled Task</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.js</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            n/a
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            n/a
         </td>
      </tr>
      <tr>
         <td rowspan=10>Manual Task</td>
         <td>Python</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Node.JS</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Java</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Go</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>.Net</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>PHP</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ruby</td>
         <td>
            <ul>
               <li>buildPackLangVersion: Language Version</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Docker</td>
         <td>
            <ul>
               <li>dockerFilePath: Path to the docker file</li>
            </ul>
         </td>
      </tr>
      <tr>
         <td>Ballerina</td>
         <td>
            n/a
         </td>
      </tr>
      <tr>
         <td>WSO2 MI</td>
         <td>
            n/a
         </td>
      </tr>
   </tbody>
</table>
