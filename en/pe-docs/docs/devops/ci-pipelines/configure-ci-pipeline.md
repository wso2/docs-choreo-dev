# Configure CI Pipeline

Choreo has built in CI pipeline so that the developers and platform engineers do not need to worry about creating docker images and saving in a container registry on their own. The developers could visit the build page from their component view and then build the component so that it would be ready for the deployment. As a platform engineer you may need to enforce configurations on this build pipeline. Each component would have its own build page, and you configure them separately.

More Information can be found in [CI-CD concept](../../choreo-concepts/ci-cd.md) document. 

## Trigger a Build

You can trigger a build from past commit or the latest commit.

1. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**. 
2. In the left navigation menu, click **DevOps** and then click **CI Pipelines**. 
3. On the **CI Pipelines** page, click **Build Latest**.

    !!! note
        The build process may take some time. You can track progress in the **Build Details** pane. Once complete, the build status changes to **Success**.


Alternatively, you can pick a specific commit and trigger a build too. Click on **Show Commits**. Select the commit you want to build and Click **Build**.

Another option you have is to build from a tag. Click on the down arrow next to **Show Commits** button. Then from the dropdown menu, select **Show Tags**. Click on **Show Tags** button.Finally, select the tag you want to pick and Click **Build**.

!!! note
        Users can trigger builds using specific tags from the connected Git repository. However, this action bypasses the standard branch-based deployment process and should only be used for critical, time-sensitive scenarios, as it can disrupt deployment track integrity.

To configure build pipeline, click **Build Configurations**. The configurations would be specific to the type of component. For certain components, you can configure build time environment variables too. 
<!-- TODO: (VirajSalaka) Find out the component types which has environment variables has support -->

### Repeatable builds

Choreo can replicate builds from an identical code version (Git commit). This means that multiple builds initiated from the same Git commit will generate Docker images with the same behavior.

## Build logs

To view Build Logs for a component,

1. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
2. In the left navigation menu, click **DevOps** and then click **CI Pipelines**. 
3. You can view build logs for specific builds on the **CI Pipelines** page.
4. To view details of a specific build, click **View Details** corresponding to the build.


## Auto Build on Commit

With Auto Build on Commit, you can configure the build pipeline to be triggered once a new commit is available in the repository's selected branch. 

1. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** list and finally the relevant **Component**. 
2. In the left navigation menu, click **DevOps** and then click **CI Pipelines**. 
3. On the **CI Pipelines** page, toggle **Auto Build on Commit** radio button.

    !!! info
        If the developer created a component by providing an opensource repository without authorizing Github, you would see a button **Configure Auto Build** instead of  **Auto Build on Commit** radio button. Click **Configure Auto Build**. Then it would redirect to github login page and ask to authorize **WSO2 Cloud App**. Once you authorize, you would be redirected back to same page. Now you would see a button **Add Repository** instead of **Configure Auto Build**. Click on **Add Repository**. It would redirect to github again asking to install and authorize WSO2 Cloud App for provided set of repositories. You can pick the repositories you would like to authorize and finally click **Install and Authorize**. After that, **Auto Build on Commit** radio button would appear. 
<!-- TODO: (VirajSalaka) link the auto deploy on build-->

<!-- TODO: (VirajSalaka) mention the limits on build images a paid user can have -->

## Configure CI Pipeline Variables and Secrets.

Choreo supports adding and updating configurations like environment variables and secrets related to your CI Pipeline. To apply such configurations to the CI pipeline of a component,

1. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
2. In the left navigation menu, click **DevOps** and then click **CI Pipelines**. 
3. On the **CI Pipelines** page, click **Build Variables and Secrets**.
4. In the slide-in window appears in right, you can add or edit environment variables. 
