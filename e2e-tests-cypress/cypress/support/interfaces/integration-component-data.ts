import { AbsComponent } from "./abs-component";

export interface IntegrationComponentData extends AbsComponent {
  projectName: string;
  componentName: string;
  srcGitRepoUrl: string;
  repositoryType: string;
  oasFilePath: string;
  repositorySubPath: string;
  accessibility: string;
  componentType: string;
  srcGitRepoBranch: string;
}
