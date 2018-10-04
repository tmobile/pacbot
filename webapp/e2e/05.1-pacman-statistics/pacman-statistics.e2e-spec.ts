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

import { browser, by, element, protractor } from 'protractor';
import { Login } from '../page-objects/login.po';
import { OverviewCompliance } from '../page-objects/overview.po';
import { Menu } from '../page-objects/menu.po';
import { PacmanStatistics } from '../page-objects/pacman-statistics.po';
const timeOutHigh = 1800000;

xdescribe('Pacman Statistics', () => {
  let login_po: Login;
  let OverviewCompliance_po: OverviewCompliance;
  let menu_po: Menu;
  let pacman_statistics_po: PacmanStatistics;
  const EC = protractor.ExpectedConditions;
  let Temp;

  beforeAll(() => {
    login_po = new Login();
    OverviewCompliance_po = new OverviewCompliance();
    pacman_statistics_po = new  PacmanStatistics();
    menu_po = new Menu();
  });
  it('Verify Pacman Statistics Header', () => {
  browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
  menu_po.MenuClick().click();
  browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
  menu_po.PacmanStatisticsClick().click();
  browser.wait(EC.visibilityOf(pacman_statistics_po.getTopHeader()), timeOutHigh);
  Temp = pacman_statistics_po.getTopHeader().getText();
  expect(Temp).toEqual('PacMan Statistics');
  pacman_statistics_po.getBackArrow().click();
  });
  it('Verify Policies Enforced Header', () => {
    browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
    menu_po.MenuClick().click();
    browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
    menu_po.PacmanStatisticsClick().click();
    browser.wait(EC.visibilityOf(pacman_statistics_po.getPoliciesEnforced()), timeOutHigh);
    Temp = pacman_statistics_po.getPoliciesEnforced().getText();
    expect(Temp).toEqual('POLICIES ENFORCED');
    pacman_statistics_po.getBackArrow().click();
    });
    it('Verify Policy Evaluations  Header', () => {
      browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
      menu_po.MenuClick().click();
      browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
      menu_po.PacmanStatisticsClick().click();
      browser.wait(EC.visibilityOf(pacman_statistics_po.getPolicyEvaluations()), timeOutHigh);
      Temp = pacman_statistics_po.getPolicyEvaluations().getText();
      expect(Temp).toEqual('POLICY EVALUATIONS');
      pacman_statistics_po.getBackArrow().click();
      });
      it('Verify Available Auto Fixes', () => {
        browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
        menu_po.MenuClick().click();
        browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
        menu_po.PacmanStatisticsClick().click();
        browser.wait(EC.visibilityOf(pacman_statistics_po.getAvailableAutoFixes()), timeOutHigh);
        Temp = pacman_statistics_po.getAvailableAutoFixes().getText();
        expect(Temp).toEqual('AVAILABLE AUTO FIXES');
        pacman_statistics_po.getBackArrow().click();
        });
        it('Verify Auto Fixes Applied  Header', () => {
          browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
          menu_po.MenuClick().click();
          browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
          menu_po.PacmanStatisticsClick().click();
          browser.wait(EC.visibilityOf(pacman_statistics_po.getAutofixesApplied()), timeOutHigh);
          Temp = pacman_statistics_po.getAutofixesApplied().getText();
          expect(Temp).toEqual('AUTO FIXES APPLIED');
          pacman_statistics_po.getBackArrow().click();
          });
        it('Verify AWS Accounts Scanned Header', () => {
            browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
            menu_po.MenuClick().click();
            browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
            menu_po.PacmanStatisticsClick().click();
            browser.wait(EC.visibilityOf(pacman_statistics_po.getAwsAccountScanned()), timeOutHigh);
            Temp = pacman_statistics_po.getAwsAccountScanned().getText();
            expect(Temp).toEqual('AWS ACCOUNTS SCANNED');
            pacman_statistics_po.getBackArrow().click();
            });
            it('Verify Events Processed Header', () => {
              browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
              menu_po.MenuClick().click();
              browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
              menu_po.PacmanStatisticsClick().click();
              browser.wait(EC.visibilityOf(pacman_statistics_po.getEventsProcessed()), timeOutHigh);
              Temp = pacman_statistics_po.getEventsProcessed().getText();
              expect(Temp).toEqual('EVENTS PROCESSED');
              pacman_statistics_po.getBackArrow().click();
              });
              it('Verify Assets Scanned  Header', () => {
                browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
                menu_po.MenuClick().click();
                browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
                menu_po.PacmanStatisticsClick().click();
                browser.wait(EC.visibilityOf(pacman_statistics_po.getAssetsScanned()), timeOutHigh);
                Temp = pacman_statistics_po.getAssetsScanned().getText();
                expect(Temp).toEqual('ASSETS SCANNED');
                pacman_statistics_po.getBackArrow().click();
                });
                it('Verify Total Violations Header', () => {
                  browser.wait(EC.presenceOf(menu_po.MenuClick()), timeOutHigh);
                  menu_po.MenuClick().click();
                  browser.wait(EC.visibilityOf(menu_po.PacmanStatisticsClick()), timeOutHigh);
                  menu_po.PacmanStatisticsClick().click();
                  browser.wait(EC.visibilityOf(pacman_statistics_po.getTotalViolations()), timeOutHigh);
                  Temp = pacman_statistics_po.getTotalViolations().getText();
                  expect(Temp).toEqual('TOTAL VIOLATIONS');
                  pacman_statistics_po.getBackArrow().click();
                  });
});
