import { Enums } from "../console/enums";

export interface ComponentData {
  projectName: string;
  componentName: string;
  displayType: Enums.DisplayType;
  triggerChannels: string;
  triggerId: string;
  srcGitRepoUrl: string;
  repositoryType: string;
  sampleTemplate: string;
  repositorySubPath: string;
  initializeAsBallerinaProject: boolean;
  accessibility: string;
}
