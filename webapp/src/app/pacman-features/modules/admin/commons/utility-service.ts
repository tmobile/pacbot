import { Injectable } from '@angular/core';

@Injectable()
export class AdminUtilityService {

    private months: any = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
    constructor() { }
    

    decodeCronExpression(expression: any): any {

        let checkForDuration = function (cronValue) {
            let arr = cronValue.split("/");
            if (arr.length > 1) {
                return arr[1];
            } else {
                return arr[0];
            }
        }

        let checkIfCronValueIsForMonthly = function (cronValue) {
            let arr = cronValue.split("/");
            if (arr.length > 1) {
                return true;
            } else {
                return false;
            }
        }

        let checkForSpecialCharactersInCron = function (cronValue) {
            return (cronValue != "*" && cronValue != "?" && cronValue != "0")
        }

        let intervals = ["Minutes", "Hourly", "Daily", "Monthly", "Weekly", "Yearly"];

        let decodedObject = {
            day: "",
            duration: "",
            interval: "",
            month: "",
            week: ""
        };

        let expressionSplitArr = expression.split(' ');

        for (let i = 0; i < expressionSplitArr.length; i++) {

            if (i === 0 || i === 1) {
                if (checkForSpecialCharactersInCron(expressionSplitArr[i])) {
                    decodedObject.duration = checkForDuration(expressionSplitArr[i]);
                    decodedObject.interval = intervals[i];
                    break;
                }
            } else if (i === 2) {
                if (checkForSpecialCharactersInCron(expressionSplitArr[i])) {
                    let j = i + 1;
                    let monthlyExpressionValue = checkForSpecialCharactersInCron(expressionSplitArr[j]);
                    if (monthlyExpressionValue) {
                        decodedObject.day = checkForDuration(expressionSplitArr[i]);
                    } else {
                        decodedObject.duration = checkForDuration(expressionSplitArr[i]);
                        decodedObject.interval = intervals[i];
                        break;
                    }
                }
            } else if (i === 3) {
                if (checkForSpecialCharactersInCron(expressionSplitArr[i])) {
                    if (checkIfCronValueIsForMonthly(expressionSplitArr[i])) {
                        decodedObject.duration = checkForDuration(expressionSplitArr[i]);
                        decodedObject.interval = intervals[i];
                    } else {
                        decodedObject.month = checkForDuration(expressionSplitArr[i]);
                        decodedObject.interval = intervals[intervals.length - 1];
                    }
                    break;
                }
            } else if (i === 4) {
                if (checkForSpecialCharactersInCron(expressionSplitArr[i])) {
                    decodedObject.week = checkForDuration(expressionSplitArr[i]);
                    decodedObject.interval = intervals[i];
                    break;
                }
            }
        }
        return decodedObject;
    }

    decodeCronJob(frequency: any): any {

        let frequencyObj: any = {};
        if (frequency) {
            frequencyObj.cronObj = this.decodeCronExpression(frequency);
            if (frequencyObj.cronObj.duration < 10 && frequencyObj.cronObj.duration != 0 && frequencyObj.cronObj.duration != '' && frequencyObj.cronObj.duration != undefined) {
                frequencyObj.cronObj.duration = "0" + frequencyObj.cronObj.duration;
            }

            if (frequencyObj.cronObj.month < 10 && frequencyObj.cronObj.month != 0 && frequencyObj.cronObj.month != '' && frequencyObj.cronObj.month != undefined) {
                frequencyObj.cronObj.month = "0" + frequencyObj.cronObj.month;
            }
            if (frequencyObj.cronObj.day < 10 && frequencyObj.cronObj.day != 0 && frequencyObj.cronObj.day != '' && frequencyObj.cronObj.day != undefined) {
                frequencyObj.cronObj.day = "0" + frequencyObj.cronObj.day;
            }
            if (frequencyObj.cronObj.month == '' || frequencyObj.cronObj.month == undefined) {
                frequencyObj.cronObj.month = 0;
            }
            if (frequencyObj.cronObj.interval == 'Minutes') {
                frequencyObj.cronObj.intervalTxt = 'minute';
                frequencyObj.cronObj.intervalVal = 'A';
            } else if (frequencyObj.cronObj.interval == 'Hourly') {
                frequencyObj.cronObj.intervalTxt = 'hour';
                frequencyObj.cronObj.intervalVal = 'B';
            } else if (frequencyObj.cronObj.interval == 'Daily') {
                frequencyObj.cronObj.intervalTxt = 'day';
                frequencyObj.cronObj.intervalVal = 'C';
            } else if (frequencyObj.cronObj.interval == 'Weekly') {
                frequencyObj.cronObj.intervalTxt = '';
                frequencyObj.cronObj.intervalVal = 'D';
            } else if (frequencyObj.cronObj.interval == 'Monthly') {
                frequencyObj.cronObj.intervalTxt = 'month';
                frequencyObj.cronObj.intervalVal = 'E';
            } else if (frequencyObj.cronObj.interval == 'Yearly') {
                frequencyObj.cronObj.intervalTxt = '';
                frequencyObj.cronObj.intervalVal = 'F';
            }
        }
        return frequencyObj.cronObj;
    }

    decodeCronToReadableText(frequency: any) {
        return this.getFrequencyInText(this.decodeCronJob(frequency));
    }

    private getFrequencyInText(frequency: any) {
        let textToDisplay = '';
        if (frequency.interval === 'Monthly') {
            textToDisplay += 'Day ' + frequency.day + ' of every ';
        }

        if (frequency.interval !== 'Monthly') {
            textToDisplay += 'Every ';
        }

        if (frequency.duration > 1) {
            textToDisplay += (frequency.duration + ' ');
        }

        if (frequency.week) {
            textToDisplay += (frequency.week + ' ');
        }

        if (frequency.month > 0 && frequency.day > 0) {
            textToDisplay += frequency.day + ' of ' + this.months[frequency.month - 1];
        }

        if (frequency.month > 0 && frequency.day > 0) {
            textToDisplay += frequency.day + ' of ' + this.months[frequency.month - 1];
        }

        textToDisplay += frequency.intervalTxt;

        if (frequency.duration > 1) {
            textToDisplay += 's';
        }
        return textToDisplay;
    }
}
