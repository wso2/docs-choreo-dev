# Manage Configurations and Secrets

Choreo allows you to easily manage and version your component's configurations and secrets as **file mounts** or **environment variables**.

!!! info "Note"
    All configurations and secrets are stored in an encrypted secret vault in the cloud data plane managed by WSO2.
    For private data planes, they are stored in your cloud environment's attached secret manager.

## The difference between configurations and secrets

Choreo treats all configurations and secrets as sensitive content, but lets you choose between secret or configuration when creating file mounts or environment variables.

- **Secrets** are write-only. Once created, you cannot view or retrieve their content via the Choreo Console, but you can overwrite them anytime.
- **Configurations** can be read and updated via the Choreo Console after creation.
  
    !!!info "Note"
          For sensitive data such as database passwords, cloud credentials, or service accounts, use secrets rather than configurations.

## Add an environment variable to your container

To add environment variables to your component:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **DevOps** and then click **CD Pipelines**.
4. Click **Manage Configs and Secrets** in the environment card.
5. Expand **Environment Variables** and click **+ Add a Configuration**.
6. Enter the variable name and value. Mark as a secret if the value is sensitive.
    
    !!!info "Note"
            
            Secret environment variables cannot be read after creation.

7. Click **Add**.
8. Click **Save and Deploy**.

## Add a file mount to your container

To add a file mount to your component:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **DevOps** and then click **CD Pipelines**.
4. Click **Manage Configs and Secrets** in the environment card.
5. Expand **File Mount** and click **+ Add a File Mount**.
6. Specify the **Mount Path** where the file should be mounted inside the container. Use an absolute path including filename and extension.
7. Enter or paste the configuration content. Mark as a secret if the content is sensitive.
    
    !!!info "Note"
            
            Secret file mounts cannot be read after creation.

8. Click **Save**.
9. Click **Save and Deploy**.

## Update an existing configuration or a secret

To update a configuration or secret:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **DevOps** and then click **CD Pipelines**.
4. Click **Manage Configs and Secrets** in the environment card.
5. To update an environment variable, expand **Environment Variables** and click the edit icon next to the variable.
   To update a file mount, expand **File Mount** and click the edit icon next to the file.
6. Click **Update**.
7. Click **Save and Deploy**.

## Delete an existing configuration or a secret

To delete a configuration or secret:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **DevOps** and then click **CD Pipelines**.
4. Click **Manage Configs and Secrets** in the environment card.
5. To delete an environment variable, expand **Environment Variables** and click the delete icon next to the variable.
   To delete a file mount, expand **File Mount** and click the delete icon next to the file.
6. Confirm the deletion when prompted.
7. Click **Save and Deploy**.

## Manage Ballerina configurables

Choreo manages [Ballerina configurables](https://ballerina.io/learn/by-example/configurable-variables/) for your Ballerina components.

You can modify Ballerina configurables via the **CD Pipelines** page when deploying or promoting a Ballerina application.
  
!!! tip
      Use configurables instead of environment variables to add file mounts to Ballerina components.
      Environment variables are primarily for components written in other languages.

!!! note
      To link configuration groups to Ballerina component configurables, click **Allow Linking Configuration Groups** at the top of the configuration form during deployment. This enables a link icon next to each configurable field, allowing you to map configuration group values.

## Alternative configuration management approach

!!! Warning "Warning"
    This alternative method is not recommended for managing configurations and secrets in Choreo.

### Add an environment variable to your container

To add environment variables:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **K8s Operations** and then click **Configs & Secrets**.
4. Click **+ Create**.
5. Select **Environment Variables** in the **Create a Config or Secret** pane.
6. Select **Mark as a Secret** if the values contain sensitive information.
    
    !!!info "Note"
           
        Secret environment variables cannot be read after creation.

7. Enter a **Display Name** to identify this configuration.

    !!!tip
        The display name is for identification only and doesn't affect the actual environment variables.

8. Enter your environment variables as key-value pairs. Click **Add Item** to create additional variables.

9. Click **Create**.

### Add a file mount to your container

To add a file mount to your component:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **K8s Operations** and then click **Configs & Secrets**.
4. Click **+ Create**.
5. Select **File Mount** in the **Create a Config or Secret** pane.
6. Select **Mark as a Secret** if the file contains sensitive information.
    
    !!! info "Note"
           
        Secret file mounts cannot be read after creation.

7. Enter a **Display Name** to identify this file mount.
  
    !!! tip

        The display name is for identification only and doesn't affect the file mount or its content.

8. Specify the **File Mount Path** where the file should be mounted inside the container. Use an absolute path including filename and extension.
  
    !!! tip

        The mount path filename doesn't need to match your configuration name or uploaded filename.

9. Either upload a configuration file or paste content directly into the editor.

10. Click **Create**.
  
    !!! info "Note"
           
        Configurations and secrets apply immediately. Your container's running replicas will undergo a rolling restart to reflect the changes.

### Update an existing configuration or secret

To update a configuration or secret:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **K8s Operations** and then click **Configs & Secrets**.
4. Click the edit icon next to the configuration or secret you want to update.
5. Make your changes and click **Save**.

### Delete an existing configuration or a secret

To delete a configuration or secret:

1. Sign in to the [Choreo Console](https://console.choreo.dev/).
2. In the Choreo Console top navigation menu, select the **Organization**, then the **Project** and finally the relevant **Component**.
3. In the left navigation menu, click **K8s Operations** and then click **Configs & Secrets**.
4. Click the delete icon next to the configuration or secret you want to delete.
5. Type the name to confirm deletion.
6. Click **Delete**.
