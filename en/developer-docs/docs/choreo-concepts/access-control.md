# Access Control

With Choreo, administrators can control user access to different projects and environments within the organization. 
At the finest granularity level, an administrator can restrict a user to perform a specific action on a specific project and a specific environment.

## Fundamental Question in Access Control

Access control in any system ultimately comes down to answering the following question.

> *Can this **user** perform this **action** on this **resource**?*

???+ example
    Can Harry view logs of development environment of engineering project? 
    
    - `Harry` is the **user**  
    - `view logs` is the **action**  
    - `development environment of engineering project` is the **resource**   

## Permission

In Choreo access control model, a permission is the right to perform a specific action. 

???+ example
    A user can create a component only if the user has `Create Component` permission.

### Environment Specific Permissions

In Choreo, actions such as `deploy component`, `promote component`, `create configuration groups`, and `view logs` must be performed within the context of an environment. The permissions which give the right to perform such actions are categorized as **Environment Specific** permissions.

!!! note
    For actions tied to an environment, it is sometimes necessary to allow a user to perform the action in certain environments but not in others; for example, a developer may be allowed to view logs in the development environment but not in the production environment.

## Role

Assigning permissions individually to each user is a time-consuming and error prone task. Users with similar job responsibilities often need the same set of permissions, which is why **roles** exist. A role is a collection of permissions designed to reflect real-world job responsibilities.

Roles simplify permission management. For example, instead of assigning 20 permissions to each of 50 developers (1000 assignments), you can assign the 20 permissions to a Developer role and then assign that role to each developer (20 + 50 assignments).  

!!! info
    Each organization in Choreo comes with a set of predefined roles with default permissions. Organization administrators can customize these roles or create new ones as needed.

## Group 

A group is a collection of users, usually organized by team or department.

Instead of assigning a role to each user, you assign the role to the group. Every user in the group automatically gets the group’s permissions. This makes updates easier. For example, instead of removing 50 role-to-user assignments and creating 50 new ones, you only need to remove one role-to-group assignment and add one new assignment.

!!! info
    Each organization in Choreo comes with a set of predefined groups. By default, each group is mapped to a role with the same name. Organization administrators can customize these groups or create new ones as needed.

## Permission to Role Assignment

In Choreo, all permission-to-role assignments apply across the organization. You can’t restrict these assignments to specific resources. For example, you can’t configure Choreo to grant the `View Logs` permission to the `Developer` role only in the Development environment of the Engineering project.

## Role to Group Assignment

The real strength of Choreo Access Control comes from role-to-group assignments. These assignments can include resource-specific restrictions. For example, you can assign the `Developer` role to the `Engineering Project Developer` group, but limit it to the Development environment of the Engineering project.

Each role-to-group assignment has two attributes:

1. **Mapping Level** : Defines whether the assignment applies to the entire `Organization` or only to a specific `Project`.
2. **Applicable Environment** : Defines whether the assignment applies to `All` environments or only to a specific `Environment`.

By combining these attributes, you can create four types of assignments.

|Assignment Type|Mapping Level|Appicable Environment|
|-|-|-|
|`Organization Scoped`|Organization|All|
|`Project Scoped`|Project|All|
|`Environement Scoped`|Organization|Environment|
|`Project-Environment Scoped`|Project|Environment|

!!! warning "Important"
    Avoid assigning multiple roles to the same group across different projects or mapping levels (organization and project). Doing so can give users unintended permissions in some projects, allowing access to tasks they shouldn’t perform. To ensure proper access control, assign only one role to a group across projects or mapping levels.

## Extent of Access granted through different Role to Group assignment types

Recall the [Fundamental Question in Access Control](#fundamental-question-in-access-control). 

> *Can this **user** perform this **action** on this **resource**?*

Based on this question, the extent of access has two parts.

1. Allowed **actions**

2. Allowed **resources** (the resources on which those actions can be performed)

Next, let’s go through each role-to-group assignment type to see the extent of access it grants. We’ll look at both the allowed **actions** and the allowed **resources**. 

### Organization Scoped Assignments

- Actions permitted by the role are allowed on resources within the organization. 
- All other actions are not allowed.

!!! note
    Actions permitted by a role are the actions linked to the permissions assigned to the role.

### Project Scoped Assignments

- Actions permitted by the role are allowed on resources within the specific project. 
- All other actions are not allowed.

### Environement Scoped Assignments

- Environment specific actions permitted by the role are allowed on resources within the specific environment of the organization.
- Other actions permitted by the role are allowed on resources within the organization.
- All other actions are not allowed.

!!! note
    **Environment specific actions** permitted by a role are the actions linked to the [**environment specific permissions**](#environment-specific-permissions) assigned to the role.

For environement scoped assignments, allowing **non environment specific** actions on resources across organization is intentional. This design aligns with real world Access Control use cases. 

!!! example
    Suppose you assign the Developer role to the Engineering Developer group, but only for the Development environment of the Engineering project. The purpose of this assignment is not to prevent developers from performing actions like `build component`, which don’t depend on a specific environment. Instead, the goal is to restrict developers from performing actions such as `promote component` on environments they aren’t authorized to access, like Production.

!!! warning "Important"
    Exercise care when creating environment scoped role-to-group assignments. Only environment specific actions are restricted to that environment; all other actions remain allowed on resources across the organization.

### Project-Environment Scoped Assignments

- Environment Specific actions permitted by the role are allowed on resources within the specific Environment of the specific Project.
- Other actions permitted by the role are allowed on resources within the specific Project.
- All other actions are not allowed.

Similar to previous case, allowing **non environment specific** actions on resources across project is intentional. This design aligns with real world Access Control use cases.

!!! warning "Important"
    Exercise care when creating project-environment scoped role-to-group assignments. Only environment specific actions are restricted to that environment; all other actions remain allowed on resources across the project.

Now that you understand the access control concepts in Choreo, see [Configure Access Control](../administer/configure-access-control.md) for a walkthrough on how to set it up.