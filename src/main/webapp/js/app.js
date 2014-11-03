/*global angular*/    // Stop jsLint from complaining about globally defined variables.

angular
.module('govna', ['ui.bootstrap', 'ui.grid', 'ui.grid.selection' ])
.controller('MainCtrl', function ($scope, $http, $location, $timeout) {
    'use strict';

    var origin = $location.protocol() + "://" + $location.host() + ":" + $location.port();
    console.log("Origin is: " + origin);

    $scope.groupName = null;
    $scope.groupData = null;
    $scope.groupList = null;

    $scope.debugIsCollapsed = true;

    $scope.message = null;
    $scope.notFound = false;

    $scope.validationCollapse = true;
    $scope.trialGroup = null;
    $scope.trialDeps = null;
    $scope.validationResponse = null;
    $scope.validationInputError = null;

    $scope.groupGridOptions =
        {
            columnDefs: [
                { name: 'group', field: 'groupName'},
                { name: 'numProhibitions', field: 'numProhibitions'},
                { name: 'numDeprecations', field: 'numDeprecations'}
            ],
            enableSorting:true,
            multiSelect: false,
            noUnselect: true,
            enableRowHeaderSelection: false,
            enableRowSelection: true
           // minRowsToShow: 5
        };

    var fillInRestrictions = function() {
        for (var i = 0, l = $scope.groupList.length; i < l; i++) {
            $scope.groupList[i].numProhibitions = countRestrictions('P', $scope.groupList[i].restrictions);
            $scope.groupList[i].numDeprecations = countRestrictions('D', $scope.groupList[i].restrictions);
        }
    };


    var countRestrictions = function(type, restrictionsList) {
        var count = 0;
        for(var i=0, l = restrictionsList.length; i < l; i++) {
            if (restrictionsList[i].type === type) {
               count++;
            }
        }
        return count;
    };

    $scope.groupNameEnterKeyHit = function() {
        if ($scope.notFound) {
            $scope.newGroupData();
        }
        else {
            $scope.fetchGroup();
        }
    };

    var createExemptConsumersString = function() {
        if ($scope.groupData.restrictions === null) {
            $scope.groupData.restrictions = [];
        }
        $scope.groupData.restrictions.forEach(function (oneRestriction, index) {
            if (oneRestriction.exemptConsumers === null) {
                oneRestriction.exemptConsumers = [];
            }
            oneRestriction.exemptConsumersString = oneRestriction.exemptConsumers.join(', ');
        });
    };

    $scope.groupGridOptions.onRegisterApi = function(gridApi) {
        $scope.gridApi = gridApi;
        gridApi.selection.on.rowSelectionChanged($scope, function (row) {
            $scope.groupName = row.entity.groupName;
            $scope.fetchGroup();
        });
    };

///////////// List groups /////////////////
    $scope.listGroups = function () {
        $scope.clearAll();
        var uri = origin + "/api/restrictions/groups/";
        console.log("Getting groups " + uri);
        $http.get(uri)
            .then(handleListGroups, handleListGroupsError);
    };

    var handleListGroups = function(response) {
        $scope.groupList = response.data;
        $scope.groupGridOptions.data = $scope.groupList.sort(function(a,b){
            if (a.groupName > b.groupName) {
                return 1;
            }
            if (a.groupName < b.groupName) {
                return -1;
            }
            return 0;
        });
        fillInRestrictions();
    };

    var handleListGroupsError = function(reason) {
        $scope.message = "HTTP " + reason.status + " - " + reason.data;
    };

///////////// Fetch group /////////////////
    $scope.fetchGroup = function () {
        $scope.clearStuff();
        if ($scope.groupName.length === 0) {
            $scope.clearGroup();
        }
        $scope.groupData = null;
        var uri = origin + "/api/restrictions/group/" + $scope.groupName;
        console.log("Getting group " + uri);
        $http.get(uri)
            .then(handleGetGroup, handleGetGroupError);
    };

    var handleGetGroup = function(response) {
        var data = response.data;
        delete data._id;
        $scope.groupData = data;
        createExemptConsumersString();
    };

    var handleGetGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.message = "HTTP " + reason.status + " - " + reason.data;
        }
    };

/////////////// Save group //////////////////

    var fixRestrictions = function() {
        $scope.groupData.restrictions.forEach(function (oneRestriction) {
            // If the form string is empty, empty out the array in the object
            if (oneRestriction.exemptConsumers !== undefined &&
                oneRestriction.exemptConsumers !== null &&
                oneRestriction.exemptConsumersString.trim() === "") {
                oneRestriction.exemptConsumers = [];
            }
            // Replace all whitespace with an empty string
            else if (oneRestriction.exemptConsumersString) {
                oneRestriction.exemptConsumersString = oneRestriction.exemptConsumersString.replace(/\s/g, '');
                oneRestriction.exemptConsumers = oneRestriction.exemptConsumersString.split(',');
            }
            delete oneRestriction.exemptConsumersString;  // delete from JSON going back to server.
        });
    };

    $scope.saveGroup = function() {
        fixRestrictions();
        $scope.saveGroupData();
    };

    $scope.saveGroupData = function() {
        var uri = origin + "/api/restrictions/group/" + $scope.groupName;
        console.log("Putting group " + uri);
        $http.put(uri, $scope.groupData)
            .then(handlePutGroup, handlePutGroupError);
    };

    var handlePutGroup = function(response) {
        $scope.message = "Group " + $scope.groupData.groupName + " inserted/updated successfully.";
        // Put the whitespace back
        createExemptConsumersString();
    };

    var handlePutGroupError = function(reason) {
        $scope.message = "HTTP " + reason.status + " - " + reason.data;
    };

/////////////// Delete group ////////////////
    $scope.deleteGroupData = function() {
        var uri = origin + "/api/restrictions/group/" + $scope.groupName;
        console.log("Deleting group " + uri);
        $http.delete(uri)
            .then(handleDeleteGroup, handleDeleteGroupError);    };

    var handleDeleteGroup = function(response) {
        var message = "Group " + $scope.groupData.groupName + " deleted successfully.";
        $scope.clearStuff();
        $scope.message = message;
    };

    var handleDeleteGroupError = function(reason) {
        if (reason.status === 404) {
            $scope.notFound = true;
        }
        else {
            $scope.message = "HTTP " + reason.status + " - " + reason.data;
        }
    };

/////////////// Perform Validation ////////////////
    $scope.trialValidation = function() {
        if (!$scope.trialGroup || !$scope.trialDeps) {
            $scope.validationInputError = "You must enter Group name and at least one Dependency.";
            return;
        }
//        debugger;
        var depsString = $scope.trialDeps.replace(/\s/g, '\n');
        var depArray = depsString.split('\n');

        for(var i=0, l = depArray.length; i < l; i++){
            if (depArray[i].split(':').length !== 3) {
                $scope.validationInputError = "Dependencies must be in the form group:artifactID:version.";
                return;
            }
        }

        var uri = origin + "/api/validation/trialValidation/";
        console.log("Trial validating " + uri);
        $scope.trialDeps = $scope.trialDeps.replace(/\s/g, '\n');
        var dependencyCoordinates = $scope.trialDeps.split('\n');
        var request = {groupRestrictions: $scope.groupData,
                       consumerGroup: $scope.trialGroup,
                       dependencyCoordinates: dependencyCoordinates
                      };
        $http.post(uri, request)
            .then(handleTrialValidation, handleTrialValidationError);
    };

    var handleTrialValidation = function(response) {
        $scope.validationResponse = response.data;
//      debugger;
    };

    var handleTrialValidationError = function(reason) {
        $scope.message = "HTTP " + reason.status + " - " + reason.data;
};

////////////////////////////////////////////
    $scope.clearAll = function () {
        $scope.groupName = null;
        $scope.clearStuff();
    };

    $scope.clearStuff = function () {
        $scope.groupData = null;
        $scope.groupList = null;
        $scope.message = null;
        $scope.notFound = false;
        $scope.clearValidation();
    };

    $scope.clearValidation = function () {
        $scope.validationResponse = null;
        $scope.validationInputError = null;
        $scope.message = null;
    };

    $scope.newGroupData = function() {
        $scope.notFound = false;
        $scope.groupData = {groupName: $scope.groupName, restrictions: []};
    };

    $scope.addElement = function(type) {
        $scope.groupData.restrictions.push({type: type});
    };

    $scope.removeElement = function(index) {
        $scope.groupData.restrictions.splice(index, 1);
    };

});
