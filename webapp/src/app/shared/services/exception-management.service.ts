import { Injectable } from '@angular/core';
import { Exception } from '../models/exception.model';
import { ExceptionInput } from '../models/exception-input.model';
import { UtilsService } from '../services/utils.service';
import * as moment from 'moment';
@Injectable()
export class ExceptionManagementService {

  constructor(
    private utils: UtilsService
  ) { }

  createDataToAddOrModifyException(input: ExceptionInput) {

    // input should be an object with below values:
    const inputToExtendException = new Exception();

    const today = new Date();
    const resetSelectedDateToExpiry = new Date();
    const minDate = new Date();

    let policy;
    if (input.ruleId && input.ruleName) {
      policy = {
        id: input.ruleId || '',
        name: input.ruleName || ''
      };
    }

    const numberOfDaysToBeAdded = today.getDate() + 1;
    minDate.setDate(numberOfDaysToBeAdded);
    resetSelectedDateToExpiry.setDate(numberOfDaysToBeAdded);
    // Set the min date to expiring in date
    inputToExtendException.minDate = minDate;
    inputToExtendException.selectedDate = moment(resetSelectedDateToExpiry).format('YYYY-MM-DD');
    if (input.exceptionEndDate) {
      const formatedDate = moment(input.exceptionEndDate).format('YYYY-MM-DD');
      inputToExtendException.selectedDate = formatedDate;
    }

    // Get year, month and date string, for calendard pre selected date
    inputToExtendException.disablePolicy = input.common.disablePolicy;
    inputToExtendException.exceptionReason = input.exceptionReason || '';
    inputToExtendException.targetTypeExemptionMap = [{}];

    inputToExtendException.targetTypeExemptionMap[0].policyIds = policy ? [policy] : [];
    inputToExtendException.targetTypeExemptionMap[0].targetType = input.common.resourceType;
    inputToExtendException.targetTypeExemptionMap[0].resourceIds = input.common.allResourceIds;

    inputToExtendException.allResourceIds = input.common.allResourceIds;
    inputToExtendException.allTargetTypes = input.common.allTargetTypes || [];
    inputToExtendException.allPolicyIds = policy ? [policy] : [];

    return inputToExtendException;
  }

}
