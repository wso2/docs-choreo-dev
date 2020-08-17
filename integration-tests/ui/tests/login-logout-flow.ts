import { Selector } from "testcafe";
import { getByText, getByLabelText } from "@testing-library/testcafe";
import { getLocation, getAccessToken } from "../src/utils/login-utils";
import page from "../src/model/page";
import * as config from "../testcafe-user-config.json";
import {logger} from '../src/utils/logger'

declare const test: TestFn;

fixture("User flows")
  .page(config.testURL)
  .beforeEach(async () => {
    await page.login();
  });

test("user login and logout redirection", async (t) => {
  logger.info("login logout flow")
  await t
    .click(getByLabelText("account of current user"))
    .expect(getByText(config.user.email).exists).ok()
    .expect(getByText("Logout").exists).ok()
    .click(getByText("Logout")).expect(getLocation()).contains(config.testURL + "login")
    .expect(getByText("Sign in with Google").exists).ok()
    .expect(getByText("Sign in with GitHub").exists).ok();
});
