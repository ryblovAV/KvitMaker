(function() {
    var app = angular.module('app', []);
    
    app.controller("unloadController",['$scope','$http', function($scope,$http) {

        $scope.month = 1;
        $scope.year = 2016;
        $scope.mkdType = "MKD";
        $scope.division = "LESK";
        $scope.premId = "";
        $scope.codeArray = "1;2;3";

        $scope.submit = function() {

            if ($scope.mkdType !== "MKD") {
                $scope.premId = ""
            }
            
            var attr = {
                month: $scope.month,
                year: $scope.year,
                mkdType: $scope.mkdType,
                division: $scope.division,
                premId: $scope.premId,
                codeArray: $scope.codeArray
            }

            console.log("load " + $scope.month + '.' + $scope.year + ", "
                + "mkd = " + $scope.mkdType + ", "
                + "division = " + $scope.division + ", "
                + "premId = " + $scope.premId + ", "
                + "code = " + $scope.codeArray);

            $http.post('/startExport',attr)
            
        }


    }]);
    
}());