
# Framework Name: Novac Automation Framework
# Author: Statim2-NOVAC
# File Name: PlanMaster.feature
# Description: Test script to verify Plan creation and edit functionality

Feature: Application Plan Master Feature
  As a user
  I want to create new Plan 
  And Edit Plan details

  @PlanMasterTest @STATIMCM-TC-460

  Scenario: Verify Plan master elements and create a new plan

    # Verify Plan master title
    #Then I verify "master name" is "PlanMaster.MasterName"
    When I verify tooltip for "Plan PLN" field on "PlanMaster" page with expected text "PlanMaster.ToolTip"
    And I click on "Plan PLN" on "PlanMaster" page
    Then I search for "PlanMaster.Search" in "PlanMaster" using "Search" and verify the results in "gridResultXPath"
    
    # Verify creation of plan master
    And I click on "Add list" on "PlanMaster" page
    #Then I should see "PlanMaster.PopupTitle" on "PlanMaster" page
    When I drag the popup with id "popupHeaderId" on "PlanMaster" page by offset X 100 and Y 50

    Then I wait for "1" seconds
    #Enter data into fields
    #When I enter "PlanMaster.PlanCode" in "CommonMasterCode" field on "PlanMaster" page
    When I enter text "PlanMaster.PlanCode" in "CommonMasterCode" field on "PlanMaster" page and match with "PlanMaster.ExpectedText"
    Then I wait for "1" seconds
    #When I enter text "PlanMaster.PlanName" in "CommonMasterName" field on "PlanMaster" page and match with "PlanMaster.ExpectedText" 
    And I enter "PlanMaster.PlanName" in "CommonMasterName" field on "PlanMaster" page

    # Enter Effective From Date for plan master
    When I select date "PlanMaster.PlanEffFrom" in "Eff from" date picker on "PlanMaster" page
    #When I select date "PlanMaster.PlanEffTo" in "Eff to" date picker on "PlanMaster" page
    Then I wait for "1" seconds

    # Save the form
    Then I click the "Save" button on "PlanMaster" page and verify toast using element "actualToastMessage" matches message from "PlanMaster.ExpectedToastMessage"
    #And I click on "Save" on "PlanMaster" page

    # Verify successful toast 
    #Then I should see "successfully toast" on "PlanMaster" page
    #Then Take screenshot "PlanCreatedSucessfully."
    
    # Wait for submission
    Then I wait for "2" seconds
    
    @EditPlanMasterTest @STATIMCM-TC-461    
    
    Scenario: Verify edit a existing plan master 
    
    #Verify creation of plan master
    And I click on "edit icon" on "PlanMaster" page
    Then I should see "Edit List" on "PlanMaster" page

 		# Verify all required fields are visible and enabled
    Then I should see "CommonMasterCode" on "PlanMaster" page
    Then I verify element "CommonMasterCode" is enabled on "PlanMaster" page

    Then I should see "CommonMasterName" on "PlanMaster" page
    Then I verify element "CommonMasterName" is enabled on "PlanMaster" page

    Then I should see "Save" on "PlanMaster" page
    Then I verify element "Save" is enabled on "PlanMaster" page

    #Enter data into fields
    When I enter "PlanMaster.PlanCode" in "CommonMasterCode" field on "PlanMaster" page
    And I enter "PlanMaster.PlanName" in "CommonMasterName" field on "PlanMaster" page
				
    #Save the form
    And I click on "Save" on "PlanMaster" page

    #Wait for submission
    Then I wait for "2" seconds

    #Verify successful toast 
    Then I should see "successfully toast" on "PlanMaster" page
    
    @WithoutMandatoryField @STATIMCM-TC-462 
    
    Scenario: Verify create a new plan Without Mandatory Fields

    #Navigate to the Master Dashboard page
    #When I click on "Master" on "DashboardPage" page

    #Verify Plan master title
    Then I verify master name is "PlanMaster.MasterName"
    And I click on "Plan PLN" on "PlanMaster" page
    
    #Verify creation of plan master
    And I click on "Add list" on "PlanMaster" page
    #Then I should see "PlanMaster.PopupTitle" on "PlanMaster" page

    #Enter non-mandatory data into fields
    When I enter "PlanMaster.PlanDesc" in "CommonMasterDescription" field on "PlanMaster" page
	
    #Save the form
    And I click on "Save" on "PlanMaster" page

    #Verify error Message 
    Then I should see "code error" on "PlanMaster" page
    Then I verify "code error" on "PlanMaster" page contains text "PlanMaster.CodeErrMsg"
    
    Then I should see "name error" on "PlanMaster" page
    Then I verify "name error" on "PlanMaster" page contains text "PlanMaster.NameErrMsg"
    
    Then I should see "effe from error" on "PlanMaster" page
    Then I verify "effe from error" on "PlanMaster" page contains text "PlanMaster.EffFromErrMsg"
    
    #Cancel the form
    And I click on "Cancel" on "PlanMaster" page
    
    #Wait for cancel
    Then I wait for "2" seconds
    #Then Take screenshot "Plan not created. An error message should be displayed." 