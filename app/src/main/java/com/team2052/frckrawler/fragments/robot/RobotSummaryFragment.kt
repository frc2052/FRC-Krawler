package com.team2052.frckrawler.fragments.robot

import android.os.Bundle
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.di.binding.NoDataParams
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment
import com.team2052.frckrawler.metric.data.Compiler
import com.team2052.frckrawler.models.Robot
import rx.Observable
import javax.inject.Inject


class RobotSummaryFragment : ListViewFragment<Map<String, String>, KeyValueListSubscriber>() {
    @Inject
    lateinit var mCompiler: Compiler
    var robot_id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        robot_id = arguments?.getLong(DatabaseActivity.PARENT_ID) ?: 0
        super.onCreate(savedInstanceState)
    }

    override fun getNoDataParams(): NoDataParams {
        return NoDataParams("No metrics found", R.drawable.ic_metric)
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out Map<String, String>> {
        return mCompiler.getCompiledMetricToHashMap(
                mCompiler.getCompiledRobotSummary(
                        robot_id, null,
                        Observable.just<Long>(robot_id)
                                .map<Robot>({ rxDbManager.robotsTable.load(it) })
                                .concatMap { robot -> rxDbManager.metricsInGame(robot.season_id, null) }
                )
        )
    }

    companion object {

        fun newInstance(robot_id: Long): RobotSummaryFragment {
            val fragment = RobotSummaryFragment()
            val args = Bundle()
            args.putLong(DatabaseActivity.PARENT_ID, robot_id)
            fragment.arguments = args
            return fragment
        }
    }
}