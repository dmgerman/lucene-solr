begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressSysoutChecks
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageTester
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_comment
comment|/**  * Can be retrieved via:  * wget --mirror -np http://archive.services.openoffice.org/pub/mirror/OpenOffice.org/contrib/dictionaries/  * Note some of the files differ only in case. This may be a problem on your operating system!  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"enable manually"
argument_list|)
annotation|@
name|SuppressSysoutChecks
argument_list|(
name|bugUrl
operator|=
literal|"prints important memory utilization stats per dictionary"
argument_list|)
DECL|class|TestAllDictionaries
specifier|public
class|class
name|TestAllDictionaries
extends|extends
name|LuceneTestCase
block|{
comment|// set this to the location of where you downloaded all the files
DECL|field|DICTIONARY_HOME
specifier|static
specifier|final
name|Path
name|DICTIONARY_HOME
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"/data/archive.services.openoffice.org/pub/mirror/OpenOffice.org/contrib/dictionaries"
argument_list|)
decl_stmt|;
DECL|field|tests
specifier|final
name|String
name|tests
index|[]
init|=
block|{
comment|/* zip file */
comment|/* dictionary */
comment|/* affix */
literal|"af_ZA.zip"
block|,
literal|"af_ZA.dic"
block|,
literal|"af_ZA.aff"
block|,
literal|"ak_GH.zip"
block|,
literal|"ak_GH.dic"
block|,
literal|"ak_GH.aff"
block|,
literal|"bg_BG.zip"
block|,
literal|"bg_BG.dic"
block|,
literal|"bg_BG.aff"
block|,
literal|"ca_ANY.zip"
block|,
literal|"catalan.dic"
block|,
literal|"catalan.aff"
block|,
literal|"ca_ES.zip"
block|,
literal|"ca_ES.dic"
block|,
literal|"ca_ES.aff"
block|,
comment|// BUG: broken flag "cop_EG.zip",                "cop_EG.dic",          "cop_EG.aff",
literal|"cs_CZ.zip"
block|,
literal|"cs_CZ.dic"
block|,
literal|"cs_CZ.aff"
block|,
literal|"cy_GB.zip"
block|,
literal|"cy_GB.dic"
block|,
literal|"cy_GB.aff"
block|,
literal|"da_DK.zip"
block|,
literal|"da_DK.dic"
block|,
literal|"da_DK.aff"
block|,
literal|"de_AT.zip"
block|,
literal|"de_AT.dic"
block|,
literal|"de_AT.aff"
block|,
literal|"de_CH.zip"
block|,
literal|"de_CH.dic"
block|,
literal|"de_CH.aff"
block|,
literal|"de_DE.zip"
block|,
literal|"de_DE.dic"
block|,
literal|"de_DE.aff"
block|,
literal|"de_DE_comb.zip"
block|,
literal|"de_DE_comb.dic"
block|,
literal|"de_DE_comb.aff"
block|,
literal|"de_DE_frami.zip"
block|,
literal|"de_DE_frami.dic"
block|,
literal|"de_DE_frami.aff"
block|,
literal|"de_DE_neu.zip"
block|,
literal|"de_DE_neu.dic"
block|,
literal|"de_DE_neu.aff"
block|,
literal|"el_GR.zip"
block|,
literal|"el_GR.dic"
block|,
literal|"el_GR.aff"
block|,
literal|"en_AU.zip"
block|,
literal|"en_AU.dic"
block|,
literal|"en_AU.aff"
block|,
literal|"en_CA.zip"
block|,
literal|"en_CA.dic"
block|,
literal|"en_CA.aff"
block|,
literal|"en_GB-oed.zip"
block|,
literal|"en_GB-oed.dic"
block|,
literal|"en_GB-oed.aff"
block|,
literal|"en_GB.zip"
block|,
literal|"en_GB.dic"
block|,
literal|"en_GB.aff"
block|,
literal|"en_NZ.zip"
block|,
literal|"en_NZ.dic"
block|,
literal|"en_NZ.aff"
block|,
literal|"eo.zip"
block|,
literal|"eo_l3.dic"
block|,
literal|"eo_l3.aff"
block|,
literal|"eo_EO.zip"
block|,
literal|"eo_EO.dic"
block|,
literal|"eo_EO.aff"
block|,
literal|"es_AR.zip"
block|,
literal|"es_AR.dic"
block|,
literal|"es_AR.aff"
block|,
literal|"es_BO.zip"
block|,
literal|"es_BO.dic"
block|,
literal|"es_BO.aff"
block|,
literal|"es_CL.zip"
block|,
literal|"es_CL.dic"
block|,
literal|"es_CL.aff"
block|,
literal|"es_CO.zip"
block|,
literal|"es_CO.dic"
block|,
literal|"es_CO.aff"
block|,
literal|"es_CR.zip"
block|,
literal|"es_CR.dic"
block|,
literal|"es_CR.aff"
block|,
literal|"es_CU.zip"
block|,
literal|"es_CU.dic"
block|,
literal|"es_CU.aff"
block|,
literal|"es_DO.zip"
block|,
literal|"es_DO.dic"
block|,
literal|"es_DO.aff"
block|,
literal|"es_EC.zip"
block|,
literal|"es_EC.dic"
block|,
literal|"es_EC.aff"
block|,
literal|"es_ES.zip"
block|,
literal|"es_ES.dic"
block|,
literal|"es_ES.aff"
block|,
literal|"es_GT.zip"
block|,
literal|"es_GT.dic"
block|,
literal|"es_GT.aff"
block|,
literal|"es_HN.zip"
block|,
literal|"es_HN.dic"
block|,
literal|"es_HN.aff"
block|,
literal|"es_MX.zip"
block|,
literal|"es_MX.dic"
block|,
literal|"es_MX.aff"
block|,
literal|"es_NEW.zip"
block|,
literal|"es_NEW.dic"
block|,
literal|"es_NEW.aff"
block|,
literal|"es_NI.zip"
block|,
literal|"es_NI.dic"
block|,
literal|"es_NI.aff"
block|,
literal|"es_PA.zip"
block|,
literal|"es_PA.dic"
block|,
literal|"es_PA.aff"
block|,
literal|"es_PE.zip"
block|,
literal|"es_PE.dic"
block|,
literal|"es_PE.aff"
block|,
literal|"es_PR.zip"
block|,
literal|"es_PR.dic"
block|,
literal|"es_PR.aff"
block|,
literal|"es_PY.zip"
block|,
literal|"es_PY.dic"
block|,
literal|"es_PY.aff"
block|,
literal|"es_SV.zip"
block|,
literal|"es_SV.dic"
block|,
literal|"es_SV.aff"
block|,
literal|"es_UY.zip"
block|,
literal|"es_UY.dic"
block|,
literal|"es_UY.aff"
block|,
literal|"es_VE.zip"
block|,
literal|"es_VE.dic"
block|,
literal|"es_VE.aff"
block|,
literal|"et_EE.zip"
block|,
literal|"et_EE.dic"
block|,
literal|"et_EE.aff"
block|,
literal|"fo_FO.zip"
block|,
literal|"fo_FO.dic"
block|,
literal|"fo_FO.aff"
block|,
literal|"fr_FR-1990_1-3-2.zip"
block|,
literal|"fr_FR-1990.dic"
block|,
literal|"fr_FR-1990.aff"
block|,
literal|"fr_FR-classique_1-3-2.zip"
block|,
literal|"fr_FR-classique.dic"
block|,
literal|"fr_FR-classique.aff"
block|,
literal|"fr_FR_1-3-2.zip"
block|,
literal|"fr_FR.dic"
block|,
literal|"fr_FR.aff"
block|,
literal|"fy_NL.zip"
block|,
literal|"fy_NL.dic"
block|,
literal|"fy_NL.aff"
block|,
literal|"ga_IE.zip"
block|,
literal|"ga_IE.dic"
block|,
literal|"ga_IE.aff"
block|,
literal|"gd_GB.zip"
block|,
literal|"gd_GB.dic"
block|,
literal|"gd_GB.aff"
block|,
literal|"gl_ES.zip"
block|,
literal|"gl_ES.dic"
block|,
literal|"gl_ES.aff"
block|,
literal|"gsc_FR.zip"
block|,
literal|"gsc_FR.dic"
block|,
literal|"gsc_FR.aff"
block|,
literal|"gu_IN.zip"
block|,
literal|"gu_IN.dic"
block|,
literal|"gu_IN.aff"
block|,
literal|"he_IL.zip"
block|,
literal|"he_IL.dic"
block|,
literal|"he_IL.aff"
block|,
literal|"hi_IN.zip"
block|,
literal|"hi_IN.dic"
block|,
literal|"hi_IN.aff"
block|,
literal|"hil_PH.zip"
block|,
literal|"hil_PH.dic"
block|,
literal|"hil_PH.aff"
block|,
literal|"hr_HR.zip"
block|,
literal|"hr_HR.dic"
block|,
literal|"hr_HR.aff"
block|,
literal|"hu_HU.zip"
block|,
literal|"hu_HU.dic"
block|,
literal|"hu_HU.aff"
block|,
literal|"hu_HU_comb.zip"
block|,
literal|"hu_HU.dic"
block|,
literal|"hu_HU.aff"
block|,
literal|"ia.zip"
block|,
literal|"ia.dic"
block|,
literal|"ia.aff"
block|,
literal|"id_ID.zip"
block|,
literal|"id_ID.dic"
block|,
literal|"id_ID.aff"
block|,
literal|"it_IT.zip"
block|,
literal|"it_IT.dic"
block|,
literal|"it_IT.aff"
block|,
literal|"ku_TR.zip"
block|,
literal|"ku_TR.dic"
block|,
literal|"ku_TR.aff"
block|,
literal|"la.zip"
block|,
literal|"la.dic"
block|,
literal|"la.aff"
block|,
literal|"lt_LT.zip"
block|,
literal|"lt_LT.dic"
block|,
literal|"lt_LT.aff"
block|,
literal|"lv_LV.zip"
block|,
literal|"lv_LV.dic"
block|,
literal|"lv_LV.aff"
block|,
literal|"mg_MG.zip"
block|,
literal|"mg_MG.dic"
block|,
literal|"mg_MG.aff"
block|,
literal|"mi_NZ.zip"
block|,
literal|"mi_NZ.dic"
block|,
literal|"mi_NZ.aff"
block|,
literal|"mk_MK.zip"
block|,
literal|"mk_MK.dic"
block|,
literal|"mk_MK.aff"
block|,
literal|"mos_BF.zip"
block|,
literal|"mos_BF.dic"
block|,
literal|"mos_BF.aff"
block|,
literal|"mr_IN.zip"
block|,
literal|"mr_IN.dic"
block|,
literal|"mr_IN.aff"
block|,
literal|"ms_MY.zip"
block|,
literal|"ms_MY.dic"
block|,
literal|"ms_MY.aff"
block|,
literal|"nb_NO.zip"
block|,
literal|"nb_NO.dic"
block|,
literal|"nb_NO.aff"
block|,
literal|"ne_NP.zip"
block|,
literal|"ne_NP.dic"
block|,
literal|"ne_NP.aff"
block|,
literal|"nl_NL.zip"
block|,
literal|"nl_NL.dic"
block|,
literal|"nl_NL.aff"
block|,
literal|"nl_med.zip"
block|,
literal|"nl_med.dic"
block|,
literal|"nl_med.aff"
block|,
literal|"nn_NO.zip"
block|,
literal|"nn_NO.dic"
block|,
literal|"nn_NO.aff"
block|,
literal|"nr_ZA.zip"
block|,
literal|"nr_ZA.dic"
block|,
literal|"nr_ZA.aff"
block|,
literal|"ns_ZA.zip"
block|,
literal|"ns_ZA.dic"
block|,
literal|"ns_ZA.aff"
block|,
literal|"ny_MW.zip"
block|,
literal|"ny_MW.dic"
block|,
literal|"ny_MW.aff"
block|,
literal|"oc_FR.zip"
block|,
literal|"oc_FR.dic"
block|,
literal|"oc_FR.aff"
block|,
literal|"pl_PL.zip"
block|,
literal|"pl_PL.dic"
block|,
literal|"pl_PL.aff"
block|,
literal|"pt_BR.zip"
block|,
literal|"pt_BR.dic"
block|,
literal|"pt_BR.aff"
block|,
literal|"pt_PT.zip"
block|,
literal|"pt_PT.dic"
block|,
literal|"pt_PT.aff"
block|,
literal|"ro_RO.zip"
block|,
literal|"ro_RO.dic"
block|,
literal|"ro_RO.aff"
block|,
literal|"ru_RU.zip"
block|,
literal|"ru_RU.dic"
block|,
literal|"ru_RU.aff"
block|,
literal|"ru_RU_ye.zip"
block|,
literal|"ru_RU_ie.dic"
block|,
literal|"ru_RU_ie.aff"
block|,
literal|"ru_RU_yo.zip"
block|,
literal|"ru_RU_yo.dic"
block|,
literal|"ru_RU_yo.aff"
block|,
literal|"rw_RW.zip"
block|,
literal|"rw_RW.dic"
block|,
literal|"rw_RW.aff"
block|,
literal|"sk_SK.zip"
block|,
literal|"sk_SK.dic"
block|,
literal|"sk_SK.aff"
block|,
literal|"sl_SI.zip"
block|,
literal|"sl_SI.dic"
block|,
literal|"sl_SI.aff"
block|,
literal|"sq_AL.zip"
block|,
literal|"sq_AL.dic"
block|,
literal|"sq_AL.aff"
block|,
literal|"ss_ZA.zip"
block|,
literal|"ss_ZA.dic"
block|,
literal|"ss_ZA.aff"
block|,
literal|"st_ZA.zip"
block|,
literal|"st_ZA.dic"
block|,
literal|"st_ZA.aff"
block|,
literal|"sv_SE.zip"
block|,
literal|"sv_SE.dic"
block|,
literal|"sv_SE.aff"
block|,
literal|"sw_KE.zip"
block|,
literal|"sw_KE.dic"
block|,
literal|"sw_KE.aff"
block|,
literal|"tet_ID.zip"
block|,
literal|"tet_ID.dic"
block|,
literal|"tet_ID.aff"
block|,
literal|"th_TH.zip"
block|,
literal|"th_TH.dic"
block|,
literal|"th_TH.aff"
block|,
literal|"tl_PH.zip"
block|,
literal|"tl_PH.dic"
block|,
literal|"tl_PH.aff"
block|,
literal|"tn_ZA.zip"
block|,
literal|"tn_ZA.dic"
block|,
literal|"tn_ZA.aff"
block|,
literal|"ts_ZA.zip"
block|,
literal|"ts_ZA.dic"
block|,
literal|"ts_ZA.aff"
block|,
literal|"uk_UA.zip"
block|,
literal|"uk_UA.dic"
block|,
literal|"uk_UA.aff"
block|,
literal|"ve_ZA.zip"
block|,
literal|"ve_ZA.dic"
block|,
literal|"ve_ZA.aff"
block|,
literal|"vi_VN.zip"
block|,
literal|"vi_VN.dic"
block|,
literal|"vi_VN.aff"
block|,
literal|"xh_ZA.zip"
block|,
literal|"xh_ZA.dic"
block|,
literal|"xh_ZA.aff"
block|,
literal|"zu_ZA.zip"
block|,
literal|"zu_ZA.dic"
block|,
literal|"zu_ZA.aff"
block|,   }
decl_stmt|;
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmp
init|=
name|LuceneTestCase
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tests
operator|.
name|length
condition|;
name|i
operator|+=
literal|3
control|)
block|{
name|Path
name|f
init|=
name|DICTIONARY_HOME
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
index|]
argument_list|)
decl_stmt|;
assert|assert
name|Files
operator|.
name|exists
argument_list|(
name|f
argument_list|)
assert|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|f
argument_list|)
init|;
name|Directory
name|tempDir
operator|=
name|getDirectory
argument_list|()
init|)
block|{
name|TestUtil
operator|.
name|unzip
argument_list|(
name|in
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|Path
name|dicEntry
init|=
name|tmp
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
name|Path
name|affEntry
init|=
name|tmp
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
operator|+
literal|2
index|]
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|dictionary
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dicEntry
argument_list|)
init|;
name|InputStream
name|affix
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|affEntry
argument_list|)
init|)
block|{
name|Dictionary
name|dic
init|=
operator|new
name|Dictionary
argument_list|(
name|tempDir
argument_list|,
literal|"dictionary"
argument_list|,
name|affix
argument_list|,
name|dictionary
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|tests
index|[
name|i
index|]
operator|+
literal|"\t"
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
argument_list|)
operator|+
literal|"\t("
operator|+
literal|"words="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|words
argument_list|)
operator|+
literal|", "
operator|+
literal|"flags="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|flagLookup
argument_list|)
operator|+
literal|", "
operator|+
literal|"strips="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|stripData
argument_list|)
operator|+
literal|", "
operator|+
literal|"conditions="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|patterns
argument_list|)
operator|+
literal|", "
operator|+
literal|"affixData="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|affixData
argument_list|)
operator|+
literal|", "
operator|+
literal|"prefixes="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|prefixes
argument_list|)
operator|+
literal|", "
operator|+
literal|"suffixes="
operator|+
name|RamUsageTester
operator|.
name|humanSizeOf
argument_list|(
name|dic
operator|.
name|suffixes
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testOneDictionary
specifier|public
name|void
name|testOneDictionary
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|tmp
init|=
name|LuceneTestCase
operator|.
name|createTempDir
argument_list|()
decl_stmt|;
name|String
name|toTest
init|=
literal|"zu_ZA.zip"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tests
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tests
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|toTest
argument_list|)
condition|)
block|{
name|Path
name|f
init|=
name|DICTIONARY_HOME
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
index|]
argument_list|)
decl_stmt|;
assert|assert
name|Files
operator|.
name|exists
argument_list|(
name|f
argument_list|)
assert|;
name|IOUtils
operator|.
name|rm
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
try|try
init|(
name|InputStream
name|in
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|f
argument_list|)
init|)
block|{
name|TestUtil
operator|.
name|unzip
argument_list|(
name|in
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
name|Path
name|dicEntry
init|=
name|tmp
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
name|Path
name|affEntry
init|=
name|tmp
operator|.
name|resolve
argument_list|(
name|tests
index|[
name|i
operator|+
literal|2
index|]
argument_list|)
decl_stmt|;
try|try
init|(
name|InputStream
name|dictionary
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|dicEntry
argument_list|)
init|;                InputStream affix = Files.newInputStream(affEntry)
empty_stmt|;
name|Directory
name|tempDir
init|=
name|getDirectory
argument_list|()
init|)
block|{
operator|new
name|Dictionary
argument_list|(
name|tempDir
argument_list|,
literal|"dictionary"
argument_list|,
name|affix
argument_list|,
name|dictionary
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|getDirectory
specifier|private
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|newDirectory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

