package wrapper

import constants.AO3Constant

class WorkWrapper(
    base_loc: String = AO3Constant.ao3_url,
    work_location: (work_id: Int, chapter_id: Int) -> String = AO3Constant.ao3_work
)