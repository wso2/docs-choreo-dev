import { Enums } from "../../commons/enums";
import { AbsComponent } from "../abs-component";
import { TestRunnerConfig } from "./testrunner-config";
import { WebappConfig } from "./webapp-config";

export interface TestRunnerComponent extends AbsComponent {
  name: string;
  displayName: string;
  description: string;
  projectId: string;
  labels: string;
  componentType: string;
  port: null;
  oasFilePath: "";
  accessibility: Enums.Accessibility;
  buildpackConfig: TestRunnerConfig;
}
