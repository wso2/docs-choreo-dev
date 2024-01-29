import { Enums } from "../../commons/enums";
import { AbsComponent } from "../abs-component";
import { ByocConfig } from "./byoc-config";

export interface ByocComponent extends AbsComponent {
  name: string;
  displayName: string;
  description: string;
  projectId: string;
  labels: string;
  componentType: string;
  port?: number;
  oasFilePath: string;
  accessibility: Enums.Accessibility;
  byocConfig: ByocConfig;
}
