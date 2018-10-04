/*
 *Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not use
 * this file except in compliance with the License. A copy of the License is located at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
 * implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { browser, protractor } from 'protractor';
import { Menu } from '../page-objects/menu.po';
import { ToolsPage } from '../page-objects/tools.po';

const timeOutHigh = 180000;

describe('ToolsPage', () => {
  let tools_po: ToolsPage;
  let menu_po: Menu;
  const EC = protractor.ExpectedConditions;

  beforeAll(() => {
    tools_po = new ToolsPage();
    menu_po = new Menu();
  });

  it('Verify tools page redirection', () => {
    browser.wait(EC.visibilityOf(menu_po.MenuClick()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.MenuClick()), timeOutHigh);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.ToolsOverview()), timeOutHigh);
    browser.wait(EC.elementToBeClickable(menu_po.ToolsOverview()), timeOutHigh);
    menu_po.ToolsOverview().click();
    browser.wait(EC.visibilityOf(tools_po.getTitle()), timeOutHigh);
    expect(tools_po.getTitle().getText()).toEqual('Tools');
  });

});
