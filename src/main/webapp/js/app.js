/*global angular*/    // Stop jsLint from complaining about globally defined variables.

angular
.module('govna', ['ui.bootstrap'])
.controller('MainCtrl', function ($scope, $http, $timeout) {
    'use strict';

    $scope.groupName = null;
    $scope.groupData = null;

    $scope.debugIsCollapsed = true;

    $scope.message = null;
    $scope.notFound = false;

    $scope.trialGroup = null;
    $scope.trialDeps  = null;
    $scope.validationResponse = null;

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

///////////// Fetch group /////////////////
    $scope.fetchGroup = function () {
        $scope.clearStuff();
        if ($scope.groupName.length === 0) {
            $scope.clearGroup();
        }
        $scope.groupData = null;
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
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
            // Replace all whitespace with an empty string
            if (oneRestriction.exemptConsumersString) {
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
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
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
        var uri = "http://localhost:8080/api/restrictions/group/" + $scope.groupName;
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
//      debugger;
        var uri = "http://localhost:8080/api/validation/trialValidation/";
        console.log("Trial validating " + uri);
        $scope.trialDeps = $scope.trialDeps.replace(/\s/g, ',');
        var dependencyCoordinates = $scope.trialDeps.split(',');
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
    $scope.clearGroup = function () {
        $scope.groupName = null;
        $scope.clearStuff();
//        $scope.groupData = null;
//        $scope.message = null;
//        $scope.notFound = false;
    };

    $scope.clearStuff = function () {
        $scope.groupData = null;
        $scope.message = null;
        $scope.notFound = false;
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
