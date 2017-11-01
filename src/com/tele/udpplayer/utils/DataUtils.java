package com.tele.udpplayer.utils;

import com.tele.udpplayer.R;
import com.tele.udpplayer.R.drawable;
import com.tele.udpplayer.bean.TeleVisionBean;

import java.util.ArrayList;
import java.util.List;

public class DataUtils {
	
	private static int pic[] = {R.drawable.tele_tf1_hd_1,R.drawable.tele_france2_hd_2,
			R.drawable.tele_france3_3,R.drawable.tele_canalplus_4,
			R.drawable.tele_france5_5,R.drawable.tele_m6_hd_6,
			R.drawable.tele_arte_hd_7,R.drawable.tele_canalplus_c8_8,
			R.drawable.tele_w9_9,R.drawable.tele_tmc_10,
			R.drawable.tele_nt1_11,R.drawable.tele_nrj12_12,
			R.drawable.tele_lcp_an_13,R.drawable.tele_ludo_14,
			R.drawable.tele_bfm_15,R.drawable.tele_cnews_16,
			R.drawable.tele_canalplus_cstar_17,R.drawable.tele_gulli_18,
			R.drawable.tele_france_o_19,R.drawable.tele_hd1_20,
			R.drawable.tele_equipe_tv_21,R.drawable.tele_6ter_22,
			R.drawable.tele_23_23,R.drawable.tele_rmc_decouverte_24,
			R.drawable.tele_cherie_25,R.drawable.tele_lci_26,
			R.drawable.tele_franceinfo_27};
	
	public static List<TeleVisionBean> getListData() {
		List<TeleVisionBean> mlist = new ArrayList<TeleVisionBean>();
		for (int i = 0; i < pic.length; i++) {
			TeleVisionBean bean = new TeleVisionBean();
			bean.setPic_id(pic[i]);
			int j= i+1;
			bean.setUrl("udp://@239.192.0."+j+":1234");
			mlist.add(bean);
		}
		return mlist;
	}

}
