import { Enums } from "../commons/enums";
import { AbsComponent } from "./abs-component";

export interface ComponentData extends AbsComponent {
  orgId?: number;
  orgHandle?: string;
  projectName?: string;
  componentName?: string;
  displayType?: Enums.DisplayType;
  triggerChannels?: string;
  triggerId?: string | null;
  srcGitRepoUrl?: string;
  repositoryType?: string;
  sampleTemplate?: string;
  repositorySubPath?: string;
  accessibility?: string;
  displayName?: string;
  initializeAsBallerinaProject: boolean;
  secretRef: string;
}
