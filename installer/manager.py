#!/usr/bin/env python

# =========================================================================
# Copyright 2018 T-Mobile, US
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# or in the "license" file accompanying this file. This file is distributed on
# an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or
# implied. See the License for the specific language governing permissions and
# limitations under the License.
# ==========================================================================

###############################################################################
# Author: Sajeer Noohukannu
# Maintainers: Sukesh Sugunan & Akash John
###############################################################################

import os
import sys
import time

if __name__ == "__main__":

    # Try to check the module can be imported
    # If the import fails then raise the Exception
    config_path = "settings.common"
    try:
        from core import autoload
    except ImportError:
        print("\n\tError: Framework kernel is not available. Please install/download it")
        sys.exit()

    autoload(config_path, sys.argv)
