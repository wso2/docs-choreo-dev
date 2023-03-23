# Configure Storage

All Choreo components are created with a **read-only file system**, and you cannot write or access a file system from your applications by default.

Volume Mounts allow you to create (temporary or persisted) writable file system locations for your applications.

## Storage (volume mount) types

| Type                              | Description                                                                                                                                                         |
| --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Empty Directory (In-Memory tmpfs) | A fast temporary in-memory (tempfs) storage location. This volume will be erased when the attached container is restarted or removed. *Available on all data planes.* |
| Empty Directory (Disk)            | A temporary storage location on disk. This volume will be destroyed when the attached container is restarted or removed. *Only available on private data planes.*   |
| Persistent Volume                 | A permanent storage location. This volume will be persisted even when the attached container is restarted or removed. *Only available on private data planes.*      |

!!! tip "The `/tmp` directory"
    All components are provided with a writable location under `/tmp` at creation, but you can configure other locations as required.


## Creating a temporary storage space for your container

Empty Directory (In-memory tmpfs or on-disk) mounts allow you to create temporary file systems that your application can read and write from. But it's important to note that these volumes are destroyed when a container is restarted or updated (*i.e. it's attached to the lifetime of a container*).

This option provides a convenient way to create a 'scratch space' to write files temporarily before they are stored on a more permanent storage location (such as a cloud-backed storage bucket).
> Eg. Unzipping a file, writing results from a memory-intensive operation to disk temporarily, a temporary local cache, etc.

1. Click the **Create** button in the Storage view.

2. Select the empty directory type (In-memory or on-disk) and give a reference name to the volume.

    ![Step 1: Create empty directory](../../assets/img/deploy/devops/storage/create-emptydir-step-1.png){.cInlineImage-full}

    !!! warning "In-memory (tmpfs) storage uses up your container's memory"
        Storage capacity for this type of volume will count against the container's memory limit.<br/>
        Uncontrolled writes to this location may starve your application process of memory and may result in the container getting killed and restarted if the memory limits are exceeded.

3. Configure the mount locations.
    ![Step 2: Create empty directory mounts](../../assets/img/deploy/devops/storage/create-emptydir-step-2.png){.cInlineImage-full}
    - You can add multiple mount locations to this volume. Type in the **mount path** and click **Add mount** to add a mount.
    - Mount paths should be *absolute file paths* and will be available to your application to read/write from.
    - Once the mount paths are added, click **Create**. The mount(s) will be applied immediately to your containers (a rolling restart will be triggered).

## Creating a persisted storage space for your container

> Persistent volume options are only available on private data planes.

1. Click the **Create** button in the Storage view.
2. Select the type **Persistent Volume**
3. Select a **Storage Class** available on the private data plane.
4. Configure the *storage capacity* (in GBs).
5. Select the *access mode*. 

    !!! note "Check the access modes supported by the selected Storage Class"
        Choreo does not check if the access mode(s) you select are supported by the selected Storage Class. These need to be checked against the cloud providers documentation.<br/>
        Invalid use of access modes may result in a runtime mount error.
    ![Create PV from storage classes](../../assets/img/deploy/devops/storage/create-pv.png){.cInlineImage-full}

6. Click **Create**. (The volume will be created and applied to your containers immediately).
