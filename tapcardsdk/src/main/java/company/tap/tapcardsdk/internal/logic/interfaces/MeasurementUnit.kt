package company.tap.tapcardsdk.internal.logic.interfaces

import company.tap.tapcardsdk.internal.logic.api.enums.Measurement


/**
 * Created by AhlaamK on 3/23/22.

Copyright (c) 2022    Tap Payments.
All rights reserved.
 **/
interface MeasurementUnit {
    /**
     * Gets measurement group.
     *
     * @return the measurement group
     */
    fun getMeasurementGroup(): Measurement
}