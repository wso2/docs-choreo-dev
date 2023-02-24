import { Enums } from "../console/enums";
import { AbsComponent } from "./abs-component";

export interface ComponentData extends AbsComponent {
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
