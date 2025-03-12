# Build Pipeline Logs in Choreo

The build pipeline logs in Choreo offer detailed insights into the build process of your components. These logs are divided into three main sections:

1. **Initialization**

This section captures logs related to setting up the environment for the build process. During this stage, the [component.yaml](https://wso2.com/choreo/docs/develop-components/manage-component-source-configurations/#overview-of-the-componentyaml-file) file is validated to ensure it meets the necessary requirements.

2. **Build**

This step involves the actual build process. Here, you can view logs specific to your component's build. Additionally, a Trivy vulnerability scan is performed to identify any potential vulnerabilities in the built image.

3. **Finalization**

This section includes logs related to the final steps of the build process. It involves cleaning up the build environment, marking the completion of the build.

To view the build pipeline logs, follow these steps:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console, select the appropriate **Organization**, **Project**, and **Component** from the top navigation menu.

!!! info "Note"
     Build pipeline logs are accessible only at the component level.

3. In the left navigation menu, click on **DevOps** and then click on **CI Pipelines** to view a list of builds for the selected component.
4. Click on the **View Details** button corresponding to the build you wish to inspect. This will display the detailed logs for that specific build.
