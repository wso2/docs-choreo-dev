import { Selector, ClientFunction } from "testcafe";
import {
  getByText,
  getByRole,
  getByLabelText,
  getByTestId,
} from "@testing-library/testcafe";
import Axios, { AxiosResponse } from "axios";
import { getLocation } from "./utils/utils";
import page from "./model/page";
import * as config from "../testcafe-user-config.json";
declare const test: TestFn;

fixture("User flows")
  .page(config.testURL)
  .beforeEach(async () => {
    await page.login();  
  });

test("deploy hello world example ", async (t) => {

  await t.expect(Selector("#backdrop-loader").exists).notOk({ timeout: 10000 });
  const f = Selector(".MuiTypography-h1", { timeout: 10000 });

  if (!(await f.exists)) {
    await t.hover(getByLabelText("more"))
    await t.click(getByLabelText("delete")).click(getByText("Delete"));
  }

  await t
    .click(getByRole("button")[0])
    .click(getByText("Create"))
    .typeText("#create-app", "e2etestapi")
    .click(getByTestId("service-create-btn")).expect(getLocation()).contains(config.testURL + "/app/e2etestapi")
    .click(getByLabelText("add")).expect(getByRole("tabpanel").textContent).contains("started HTTP/WS listener", { timeout: 300000 });

  const url = await Selector("#test-invoke-url").value;
  const res: AxiosResponse = await Axios.get(url + "/hello/sayHello");
  await t.expect(res.data).eql("Hello, World!");
});
