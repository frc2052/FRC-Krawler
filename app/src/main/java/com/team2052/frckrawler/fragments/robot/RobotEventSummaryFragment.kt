package com.team2052.frckrawler.fragments.robot

import android.os.Bundle
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.di.binding.NoDataParams
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment
import com.team2052.frckrawler.metric.data.Compiler
import com.team2052.frckrawler.models.Metric
import com.team2052.frckrawler.models.Robot
import rx.Observable
import javax.inject.Inject

class RobotEventSummaryFragment : ListViewFragment<Map<String, String>, KeyValueListSubscriber>() {
    @Inject
    lateinit var mCompiler: Compiler
    private var mRobot_id: Long = 0
    private var mEvent_id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        mRobot_id = arguments.getLong(DatabaseActivity.PARENT_ID)
        mEvent_id = arguments.getLong(EVENT_ID)
        super.onCreate(savedInstanceState)
    }

    override fun getNoDataParams(): NoDataParams {
        return NoDataParams("No metrics found", R.drawable.ic_metric)
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out Map<String, String>> {
        return mCompiler.getCompiledMetricToHashMap(mCompiler.getCompiledRobotSummary(mRobot_id, mEvent_id,
                Observable.just(mRobot_id)
                        .map<Robot> { rxDbManager.robotsTable.load(it) }
                        .concatMap<List<Metric>> { robot -> rxDbManager.metricsInGame(robot.season_id, null) }))
    }

    companion object {
        val EVENT_ID = "EVENT_ID"

        fun newInstance(robot_id: Long, event_id: Long): RobotEventSummaryFragment {
            val fragment = RobotEventSummaryFragment()
            val args = Bundle()
            args.putLong(DatabaseActivity.PARENT_ID, robot_id)
            args.putLong(EVENT_ID, event_id)
            fragment.arguments = args
            return fragment
        }
    }
}
