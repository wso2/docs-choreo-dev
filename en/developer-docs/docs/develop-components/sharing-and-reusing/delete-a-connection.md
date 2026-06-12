# Delete a Connection

When you no longer need a connection, you can delete it from the {{ product_name }} Console. To prevent runtime failures, {{ product_name }} does not allow you to delete a connection that is still in use by one or more components.

## Delete a connection

Follow these steps to delete a connection:

1. In the {{ product_name }} Console, go to the top navigation menu and select the organization, project, and (for component connections) the component associated with the connection.
2. In the left navigation menu, click **Connections**. This page lists all the existing connections.
3. In the row of the connection you want to delete, click the delete icon.

    When you click the delete icon, {{ product_name }} checks whether the connection has any active usages:

    - If no components are using the connection, {{ product_name }} prompts you to confirm the deletion. Follow the instructions in the confirmation dialog to confirm, and {{ product_name }} deletes the connection.
    - If one or more components are using the connection, {{ product_name }} displays the list of components that currently depend on it and prevents the deletion. To proceed, follow the steps below.

## Delete a connection that is in use

When a connection has active usages, you must remove the connection references from each of the components that depend on it before you can delete the connection.

To remove a connection reference from a component, follow these steps:

1. In the list of usages displayed in the {{ product_name }} Console, identify each component that uses the connection.
2. Go to the source repository of the component and open its `component.yaml` file.
3. Under the `dependencies` section, remove the `connectionReferences` entry that refers to the connection you want to delete.

    For example, to remove the following connection reference, delete the corresponding item under `connectionReferences`:

    ``` yaml
    dependencies:
        connectionReferences:
        - name: <CONNECTION_NAME>
          resourceRef: <RESOURCE_IDENTIFIER>
    ```

    !!! note
        If `connectionReferences` no longer contains any items after you remove the entry, you can remove the empty `connectionReferences` section as well.

4. Commit and push the changes to the source repository.
5. Build the component with the latest changes and deploy it. This ensures that the component no longer depends on the connection.
6. Repeat the above steps for every component listed as a usage of the connection.

Once you have removed the connection references from all the components that use the connection and deployed the latest changes, retry the deletion:

1. In the left navigation menu, click **Connections**.
2. In the row of the connection you want to delete, click the delete icon.
3. Follow the instructions in the confirmation dialog to confirm the deletion.

Because the connection no longer has any active usages, {{ product_name }} now deletes it.
