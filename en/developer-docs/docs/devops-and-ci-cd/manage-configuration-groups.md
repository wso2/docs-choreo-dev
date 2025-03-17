
# Manage Configuration Groups

Choreo allows you to create Configuration Groups to efficiently manage reusable configurations across components within your organization. A Configuration Group is a collection of key-value pairs, where values can be defined for multiple environments. This feature ensures consistency and simplifies the management of configurations across environments.

Configuration groups can be defined at organization level and link to components at deployment time. Once linked, Choreo automatically resolves and mounts the configurations to the respective environments on deployment. You can either link a configuration group to inject the configurations as environment variables or file mounts.

!!!important
    - All configuration group values are encrypted and stored in environment-specific key vaults.
    - Management of configuration groups is restricted to users with Choreo Admin, DevOps, and Platform Engineer roles.
    - Developers can discover configuration groups available within the organization via the **Choreo Internal Marketplace**.

## Create a configuration group

To create a new configuration group, follow the steps given below:

1. In the [Choreo Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Configuration Groups**.
3. On the **Configuration Groups** page, click **Create** and specify the following details to create a new configuration group:
   
    - **Name**: A name for the configuration group (Unique within the organization).
    - **Description**: A description for the configuration group (Optional).
    - **Define Keys**: Define the keys for the configuration group.

        - Configuration keys uniquely identify values in a configuration group. You can map these keys to environment variables or file mounts during deployment. Each key must be unique within the group.

    - **Assign Values**: Define values by environment for the keys defined.

        - By default, all the environments are grouped together allowing you to manage configuration smoothly. You can separate and manage configuration values for each environment as needed.

    - **Create**: Click **Create** to create the configuration group. 
    
4. Now you can link this configuration group to any component within the organization.

!!!note
    - Configuration groups created will be listed in the **Choreo Internal Marketplace**, improving visibility and discoverability for developers.
    - All configuration groups will also be listed in the component deployment drawers, allowing developers to easily link them during deployment.

## Link and use configuration groups

The configuration groups created at organization level can be linked to any component within the organization. A configuration group can be linked as **Environment Variables** or **File Mounts** at deployment time.

Linking a configuration group will inject the values defined in the group during deployment. The values are mapped to environment variable names or file names based on the keys defined in the configuration group. If needed, you can customize the environment variable name or file name by updating the mapping at deployment.

To link a configuration group to a component, follow the steps given below:

1. Navigate to the component you want to link the configuration group.
2. On the **Deploy** page, click **Configure & Deploy**, this will open the configuration and deployment wizard.
3. In the wizard, link the configuration groups as **Environment Variables** or **File Mounts**, based on your requirements.

    === "Environment Variables"

        - Choose the configuration group you want to link to the component.
        - Click **Link** to link the configuration group to the component.

    === "File Mounts"

        - Choose the configuration group you want to link to the component.
        - Specify the **Mount Path** to mount the configuration files.
            
            !!!note
                All configurations within the selected configuration group will be mounted as individual files to the specified mount path/directory.

        - Click **Link** to link the configuration group to the component.

4. Complete the deployment wizard by providing the required details and click **Deploy** to deploy the component with the updated configurations.

## View & edit a configuration group

To view & edit a configuration group, follow the steps given below:

1. In the [Choreo Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Configuration Groups**. 
3. In the **Configuration Groups** list, select the desired configuration group to view.

    !!!note
        - Only non-sensitive configuration values are displayed in the view mode.
        - Updating the configuration group will not affect the current deployment; changes will be applied when the component is redeployed.

### Edit the configuration group

Configuration keys and values within a configuration group can be modified, and these changes will take effect when the components using the configuration group are redeployed.

To edit the configuration group definition, click **Edit the Configuration Group** and make the necessary updates:

- Add or remove configuration keys.
- Update the configuration group's display name and description.

To edit the configuration values, click the edit icon in the corresponding set of environments and modify the required details:

- Update configuration values.
- Add a new set of configuration values.
- Add or remove environments from an existing set.

!!! warning
    - **Adding a new environment:** Non-sensitive configuration values will be copied to the new environment, but sensitive values will not be. As a result, sensitive values will be cleared across all environments in the set. **New values must be provided for sensitive configurations.**
    - **Removing an environment:** All configuration values for the removed environment will be deleted.

## Delete a configuration group

To delete a configuration group, follow the steps given below:

!!! warning
    Deleting a configuration group is a permanent, non-reversible action. Ensure that the configuration group is not linked to any component before deleting it.

1. In the [Choreo Console](https://console.choreo.dev/), go to the top navigation menu. Click **Organization** and select your organization.
2. In the left navigation menu, click **DevOps** and then click **Configuration Groups**. 
3. In the **Configuration Groups** list, click the delete icon next to the configuration group you want to delete. This will display a confirmation dialog with details about the impact of the deletion.
4. Review the details, then type the configuration group name to confirm the deletion.
5. Click **Delete**.
