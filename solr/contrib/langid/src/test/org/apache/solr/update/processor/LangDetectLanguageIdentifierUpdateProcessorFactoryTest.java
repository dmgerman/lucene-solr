begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.update.processor
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
operator|.
name|processor
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|ModifiableSolrParams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|LangDetectLanguageIdentifierUpdateProcessorFactoryTest
specifier|public
class|class
name|LangDetectLanguageIdentifierUpdateProcessorFactoryTest
extends|extends
name|LanguageIdentifierUpdateProcessorFactoryTestCase
block|{
annotation|@
name|Override
DECL|method|createLangIdProcessor
specifier|protected
name|LanguageIdentifierUpdateProcessor
name|createLangIdProcessor
parameter_list|(
name|ModifiableSolrParams
name|parameters
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|LangDetectLanguageIdentifierUpdateProcessor
argument_list|(
name|_parser
operator|.
name|buildRequestFrom
argument_list|(
literal|null
argument_list|,
name|parameters
argument_list|,
literal|null
argument_list|)
argument_list|,
name|resp
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|// this one actually works better it seems with short docs
annotation|@
name|Override
DECL|method|tooShortDoc
specifier|protected
name|SolrInputDocument
name|tooShortDoc
parameter_list|()
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|addField
argument_list|(
literal|"text"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
comment|/* we don't return 'un' for the super-short one (this detector things hungarian?).    * replace this with japanese    */
annotation|@
name|Test
annotation|@
name|Override
DECL|method|testLangIdGlobal
specifier|public
name|void
name|testLangIdGlobal
parameter_list|()
throws|throws
name|Exception
block|{
name|parameters
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|parameters
operator|.
name|add
argument_list|(
literal|"langid.fl"
argument_list|,
literal|"name,subject"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|add
argument_list|(
literal|"langid.langField"
argument_list|,
literal|"language_s"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|add
argument_list|(
literal|"langid.fallback"
argument_list|,
literal|"un"
argument_list|)
expr_stmt|;
name|liProcessor
operator|=
name|createLangIdProcessor
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"no"
argument_list|,
literal|"id"
argument_list|,
literal|"1no"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene er et fri/Ã¥pen kildekode programvarebibliotek for informasjonsgjenfinning, opprinnelig utviklet i programmeringssprÃ¥ket Java av Doug Cutting. Lucene stÃ¸ttes av Apache Software Foundation og utgis under Apache-lisensen."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"en"
argument_list|,
literal|"id"
argument_list|,
literal|"2en"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Apache Lucene is a free/open source information retrieval software library, originally created in Java by Doug Cutting. It is supported by the Apache Software Foundation and is released under the Apache Software License."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"sv"
argument_list|,
literal|"id"
argument_list|,
literal|"3sv"
argument_list|,
literal|"name"
argument_list|,
literal|"Maven"
argument_list|,
literal|"subject"
argument_list|,
literal|"Apache Maven Ã¤r ett verktyg utvecklat av Apache Software Foundation och anvÃ¤nds inom systemutveckling av datorprogram i programsprÃ¥ket Java. Maven anvÃ¤nds fÃ¶r att automatiskt paketera (bygga) programfilerna till en distribuerbar enhet. Maven anvÃ¤nds inom samma omrÃ¥de som Apache Ant men dess byggfiler Ã¤r deklarativa till skillnad ifrÃ¥n Ants skriptbaserade."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"es"
argument_list|,
literal|"id"
argument_list|,
literal|"4es"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene es un API de cÃ³digo abierto para recuperaciÃ³n de informaciÃ³n, originalmente implementada en Java por Doug Cutting. EstÃ¡ apoyado por el Apache Software Foundation y se distribuye bajo la Apache Software License. Lucene tiene versiones para otros lenguajes incluyendo Delphi, Perl, C#, C++, Python, Ruby y PHP."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"ja"
argument_list|,
literal|"id"
argument_list|,
literal|"5ja"
argument_list|,
literal|"name"
argument_list|,
literal|"Japanese"
argument_list|,
literal|"subject"
argument_list|,
literal|"æ¥æ¬èªï¼ã«ã»ãããã«ã£ã½ããï¼ã¯ä¸»ã¨ãã¦ãæ¥æ¬ã§ä½¿ç¨ããã¦ããè¨èªã§ãããæ¥æ¬å½ã¯æ³ä»¤ä¸ãå¬ç¨èªãæè¨ãã¦ããªãããäºå®ä¸ã®å¬ç¨èªã¨ãªã£ã¦ãããå­¦æ ¡æè²ã®ãå½èªãã§æããããã"
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"th"
argument_list|,
literal|"id"
argument_list|,
literal|"6th"
argument_list|,
literal|"name"
argument_list|,
literal|"à¸à¸à¸à¸§à¸²à¸¡à¸à¸±à¸à¸ªà¸£à¸£à¹à¸à¸·à¸­à¸à¸à¸µà¹"
argument_list|,
literal|"subject"
argument_list|,
literal|"à¸­à¸±à¸à¹à¸à¸­à¸¥à¸µà¸ª à¸¡à¸²à¸£à¸µ à¸­à¸±à¸à¹à¸à¸­ à¸à¸£à¸±à¸à¸à¹ à¸«à¸£à¸·à¸­à¸¡à¸±à¸à¸£à¸¹à¹à¸à¸±à¸à¹à¸à¸ à¸²à¸©à¸²à¹à¸à¸¢à¸§à¹à¸² à¹à¸­à¸à¸à¹ à¹à¸à¸£à¸à¸à¹ à¹à¸à¹à¸à¹à¸à¹à¸à¸«à¸à¸´à¸à¸à¸²à¸§à¸¢à¸´à¸§ à¹à¸à¸´à¸à¸à¸µà¹à¹à¸¡à¸·à¸­à¸à¹à¸à¸£à¸à¸à¹à¹à¸à¸´à¸£à¹à¸ à¸à¸£à¸°à¹à¸à¸¨à¹à¸¢à¸­à¸£à¸¡à¸à¸µ à¹à¸à¸­à¸¡à¸µà¸à¸·à¹à¸­à¹à¸ªà¸µà¸¢à¸à¹à¸à¹à¸à¸à¸±à¸à¹à¸à¸à¸²à¸à¸°à¸à¸¹à¹à¹à¸à¸µà¸¢à¸à¸à¸±à¸à¸à¸¶à¸à¸à¸£à¸°à¸à¸³à¸§à¸±à¸à¸à¸¶à¹à¸à¸à¹à¸­à¸¡à¸²à¹à¸à¹à¸£à¸±à¸à¸à¸²à¸£à¸à¸µà¸à¸´à¸¡à¸à¹à¹à¸à¹à¸à¸«à¸à¸±à¸à¸ªà¸·à¸­ à¸à¸£à¸£à¸¢à¸²à¸¢à¹à¸«à¸à¸¸à¸à¸²à¸£à¸à¹à¸à¸à¸°à¸«à¸¥à¸à¸à¹à¸­à¸à¸à¸±à¸§à¸à¸²à¸à¸à¸²à¸£à¸¥à¹à¸²à¸à¸²à¸§à¸¢à¸´à¸§à¹à¸à¸à¸£à¸°à¹à¸à¸¨à¹à¸à¹à¸à¸­à¸£à¹à¹à¸¥à¸à¸à¹ à¸£à¸°à¸«à¸§à¹à¸²à¸à¸à¸µà¹à¸à¸¹à¸à¹à¸¢à¸­à¸£à¸¡à¸à¸µà¹à¸à¹à¸²à¸à¸£à¸­à¸à¸à¸£à¸­à¸à¹à¸à¸à¹à¸§à¸à¸ªà¸à¸à¸£à¸²à¸¡à¹à¸¥à¸à¸à¸£à¸±à¹à¸à¸à¸µà¹à¸ªà¸­à¸"
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"ru"
argument_list|,
literal|"id"
argument_list|,
literal|"7ru"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"The Apache Lucene â ÑÑÐ¾ ÑÐ²Ð¾Ð±Ð¾Ð´Ð½Ð°Ñ Ð±Ð¸Ð±Ð»Ð¸Ð¾ÑÐµÐºÐ° Ð´Ð»Ñ Ð²ÑÑÐ¾ÐºÐ¾ÑÐºÐ¾ÑÐ¾ÑÑÐ½Ð¾Ð³Ð¾ Ð¿Ð¾Ð»Ð½Ð¾ÑÐµÐºÑÑÐ¾Ð²Ð¾Ð³Ð¾ Ð¿Ð¾Ð¸ÑÐºÐ°, Ð½Ð°Ð¿Ð¸ÑÐ°Ð½Ð½Ð°Ñ Ð½Ð° Java. ÐÐ¾Ð¶ÐµÑ Ð±ÑÑÑ Ð¸ÑÐ¿Ð¾Ð»ÑÐ·Ð¾Ð²Ð°Ð½Ð° Ð´Ð»Ñ Ð¿Ð¾Ð¸ÑÐºÐ° Ð² Ð¸Ð½ÑÐµÑÐ½ÐµÑÐµ Ð¸ Ð´ÑÑÐ³Ð¸Ñ Ð¾Ð±Ð»Ð°ÑÑÑÑ ÐºÐ¾Ð¼Ð¿ÑÑÑÐµÑÐ½Ð¾Ð¹ Ð»Ð¸Ð½Ð³Ð²Ð¸ÑÑÐ¸ÐºÐ¸ (Ð°Ð½Ð°Ð»Ð¸ÑÐ¸ÑÐµÑÐºÐ°Ñ ÑÐ¸Ð»Ð¾ÑÐ¾ÑÐ¸Ñ)."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"de"
argument_list|,
literal|"id"
argument_list|,
literal|"8de"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene ist ein Freie-Software-Projekt der Apache Software Foundation, das eine Suchsoftware erstellt. Durch die hohe LeistungsfÃ¤higkeit und Skalierbarkeit kÃ¶nnen die Lucene-Werkzeuge fÃ¼r beliebige ProjektgrÃ¶Ãen und Anforderungen eingesetzt werden. So setzt beispielsweise Wikipedia Lucene fÃ¼r die Volltextsuche ein. Zudem verwenden die beiden Desktop-Suchprogramme Beagle und Strigi eine C#- bzw. C++- Portierung von Lucene als Indexer."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"fr"
argument_list|,
literal|"id"
argument_list|,
literal|"9fr"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene est un moteur de recherche libre Ã©crit en Java qui permet d'indexer et de rechercher du texte. C'est un projet open source de la fondation Apache mis Ã  disposition sous licence Apache. Il est Ã©galement disponible pour les langages Ruby, Perl, C++, PHP."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"nl"
argument_list|,
literal|"id"
argument_list|,
literal|"10nl"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene is een gratis open source, tekst gebaseerde information retrieval API van origine geschreven in Java door Doug Cutting. Het wordt ondersteund door de Apache Software Foundation en is vrijgegeven onder de Apache Software Licentie. Lucene is ook beschikbaar in andere programeertalen zoals Perl, C#, C++, Python, Ruby en PHP."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"it"
argument_list|,
literal|"id"
argument_list|,
literal|"11it"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Lucene Ã¨ una API gratuita ed open source per il reperimento di informazioni inizialmente implementata in Java da Doug Cutting. Ã supportata dall'Apache Software Foundation ed Ã¨ resa disponibile con l'Apache License. Lucene Ã¨ stata successivamente reimplementata in Perl, C#, C++, Python, Ruby e PHP."
argument_list|)
expr_stmt|;
name|assertLang
argument_list|(
literal|"pt"
argument_list|,
literal|"id"
argument_list|,
literal|"12pt"
argument_list|,
literal|"name"
argument_list|,
literal|"Lucene"
argument_list|,
literal|"subject"
argument_list|,
literal|"Apache Lucene, ou simplesmente Lucene, Ã© um software de busca e uma API de indexaÃ§Ã£o de documentos, escrito na linguagem de programaÃ§Ã£o Java. Ã um software de cÃ³digo aberto da Apache Software Foundation licenciado atravÃ©s da licenÃ§a Apache."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

