(function() {
    var app = angular.module('app',[]);
    
    app.controller(
        "unloadController",
        ['$scope','$http','$window','$interval', function($scope,$http,$window,$interval) {

        $scope.month = new Date().getMonth();
        $scope.year = new Date().getFullYear();
        $scope.mkdType = "MKD";
        $scope.division = "LESK";
        $scope.premId = "";
        $scope.codeArray = "01;02;03;04;05;06;07;08;09;10";
        $scope.message = "";
        $scope.progressArray = [];
        $scope:processId = "";
        $scope.getArchiveDisabled = true;
        $scope.archiveMessage = "";
        $scope.saveArchive = true; 
        $scope.orderByIndex = "0";

        $scope.submit = function() {

            if ($scope.mkdType !== "MKD") {
                $scope.premId = ""
            }
            
            if (($scope.mkdType == "MKD") && ($scope.premId != undefined) && ($scope.premId.trim() != "")) {
                $scope.codeArray = [];
            }             

            var attr = {
                month: $scope.month,
                year: $scope.year,
                mkdType: $scope.mkdType,
                division: $scope.division,
                premId: $scope.premId,
                codeArrayStr: $scope.codeArray,
                orderByIndex: parseInt($scope.orderByIndex)
            }

            console.log("load " + $scope.month + '.' + $scope.year + ", "
                + "mkd = " + $scope.mkdType + ", "
                + "division = " + $scope.division + ", "
                + "premId = " + $scope.premId + ", "
                + "code = " + $scope.codeArray);

            $scope.message = check()
            if ($scope.message === '') {
                console.log("send post export");
                
                $scope.getArchiveDisabled = true;
                $scope.archiveMessage = "";
                $scope.saveArchive = true;
                
                $http.post('/startProcess',attr).success(
                    function (result) {
                        if (result.error !== "") {
                            console.log("error: " + result.error)
                            $scope.message = result.error;
                        } else {
                            console.log(result);
                            $scope.archiveMessage = "Выполняется процесс выгрузки (id = " + result.processId + ")";
                            $scope.progressArray = [];
                            $scope.processId = result.processId;
                        }
                    })
            } else {
                console.log("error: " + $scope.message)
            }
        }

        function isNormalInteger(str) {
            var n = ~~Number(str);
            return String(n) === String(Number(str)) && n > 0;
        }

        function check() {

            var s = $scope.codeArray.split(";")
            for (i = 0; i < s.length; i++) {
              if (s[i].length != 2)
                  return "значение атрибута 'код участка' = '" + s[i] + "' - должно содержать 2 символа";
              if (!isNormalInteger(s[i]))
                  return "значение атрибута 'код участка' = '" + s[i] + "' - не является положительным целым числом";
            }

            var year = new Date().getFullYear();
            var minYear = year - 10;
            var maxYear = year + 10;

            if ((Number($scope.year) < minYear) || (Number($scope.year) > maxYear))
                return "значение атрибута 'год' = '" + $scope.year + "' - не находиться в диапазоне: " + minYear + " .. " + maxYear;

            if ((Number($scope.month) < 1) || (Number($scope.month) > 12))
                return "значение атрибута 'месяц' = '" + $scope.month + "' - не находиться в диапазоне: 1 .. 12";

            return "";
        }
            
        $scope.getArchive = function() {
            console.log("call getArchive processId = "+$scope.processId);
            if (($scope.processId != undefined) && ($scope.processId != '')) {
                $window.location.href = '/result?processId='+$scope.processId;
            }
        }

        $interval(
            function() {
                console.log("processId = " + $scope.processId)
                if (($scope.processId != undefined) && ($scope.processId !== "")) {
                    console.log("processId = " + $scope.processId);
                    $http.get('/progressInfo?processId='+$scope.processId).
                    success(
                        function(result) {
                            console.log(result);
                            $scope.progressArray = result;
                        }
                    );
                    $http.get('/archiveFileName?processId='+$scope.processId).
                    success(
                        function(fileName) {
                            console.log(fileName);
                            if ((fileName != undefined) && (fileName != '')) {
                                console.log("fileName = "+fileName);
                                $scope.getArchiveDisabled = false;
                                $scope.archiveMessage = "Выгрузка завершена";
                                
                                if ($scope.saveArchive) {
                                    $scope.saveArchive = false;
                                    $scope.getArchive();
                                }
                                
                            }
                        }
                    );
                }
            },
            10000
        )

    }]);

}());