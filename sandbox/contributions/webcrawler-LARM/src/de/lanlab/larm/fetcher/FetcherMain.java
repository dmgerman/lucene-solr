begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|ThreadPoolObserver
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|threads
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|storage
operator|.
name|*
import|;
end_import

begin_import
import|import
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|HTTPClient
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|MalformedPatternException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|UIManager
import|;
end_import

begin_comment
comment|/**  * ENTRY POINT: this class contains the main()-method of the application, does  * all the initializing and optionally connects the fetcher with the GUI.  *  * @author    Clemens Marschner  * @created   December 16, 2000  * @version $Id$  */
end_comment

begin_class
DECL|class|FetcherMain
specifier|public
class|class
name|FetcherMain
block|{
comment|/**      * the main message pipeline      */
DECL|field|messageHandler
specifier|protected
name|MessageHandler
name|messageHandler
decl_stmt|;
comment|/**      * this filter records all incoming URLs and filters everything it already      * knows      */
DECL|field|urlVisitedFilter
specifier|protected
name|URLVisitedFilter
name|urlVisitedFilter
decl_stmt|;
comment|/**      * the scope filter filters URLs that fall out of the scope given by the      * regular expression      */
DECL|field|urlScopeFilter
specifier|protected
name|URLScopeFilter
name|urlScopeFilter
decl_stmt|;
comment|/*      * The DNS resolver was supposed to hold the host addresses for all hosts      * this is done by URL itself today      *      * protected DNSResolver dnsResolver;      */
comment|/**      * the robot exclusion filter looks if a robots.txt is present on a host      * before it is first accessed      */
DECL|field|reFilter
specifier|protected
name|RobotExclusionFilter
name|reFilter
decl_stmt|;
comment|/**      * the host manager keeps track of all hosts and is used by the filters.      */
DECL|field|hostManager
specifier|protected
name|HostManager
name|hostManager
decl_stmt|;
comment|/**      * the host resolver can change a host that occurs within a URL to a different      * host, depending on the rules specified in a configuration file      */
DECL|field|hostResolver
specifier|protected
name|HostResolver
name|hostResolver
decl_stmt|;
comment|/**      * this rather flaky filter just filters out some URLs, i.e. different views      * of Apache the apache DirIndex module. Has to be made      * configurable in near future      */
DECL|field|knownPathsFilter
specifier|protected
name|KnownPathsFilter
name|knownPathsFilter
decl_stmt|;
comment|/**      * the URL length filter filters URLs that are too long, i.e. because of errors      * in the implementation of dynamic web sites      */
DECL|field|urlLengthFilter
specifier|protected
name|URLLengthFilter
name|urlLengthFilter
decl_stmt|;
comment|/**      * this is the main document fetcher. It contains a thread pool that fetches the      * documents and stores them      */
DECL|field|fetcher
specifier|protected
name|Fetcher
name|fetcher
decl_stmt|;
comment|/**      * the thread monitor once was only a monitoring tool, but now has become a      * vital part of the system that computes statistics and      * flushes the log file buffers      */
DECL|field|monitor
specifier|protected
name|ThreadMonitor
name|monitor
decl_stmt|;
comment|/**      * the storage is a central class that puts all fetched documents somewhere.      * Several differnt implementations exist.      */
DECL|field|storage
specifier|protected
name|DocumentStorage
name|storage
decl_stmt|;
comment|/**      * initializes all classes and registers anonymous adapter classes as      * listeners for fetcher events.      *      * @param nrThreads  number of fetcher threads to be created      */
DECL|method|FetcherMain
specifier|public
name|FetcherMain
parameter_list|(
name|int
name|nrThreads
parameter_list|,
name|String
name|hostResolverFile
parameter_list|)
throws|throws
name|Exception
block|{
comment|// to make things clear, this method is commented a bit better than
comment|// the rest of the program...
comment|// this is the main message queue. handlers are registered with
comment|// the queue, and whenever a message is put in it, the message is passed to the
comment|// filters in a "chain of responibility" manner. Every listener can decide
comment|// to throw the message away
name|messageHandler
operator|=
operator|new
name|MessageHandler
argument_list|()
expr_stmt|;
comment|// the storage is the class which saves a WebDocument somewhere, no
comment|// matter how it does it, whether it's in a file, in a database or
comment|// whatever
comment|// example for the (very slow) SQL Server storage:
comment|// this.storage = new SQLServerStorage("sun.jdbc.odbc.JdbcOdbcDriver","jdbc:odbc:search","sa","...",nrThreads);
comment|// the LogStorage used here does extensive logging. It logs all links and
comment|// document information.
comment|// it also saves all documents to page files.
name|File
name|logsDir
init|=
operator|new
name|File
argument_list|(
literal|"logs"
argument_list|)
decl_stmt|;
name|logsDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
comment|// ensure log directory exists
comment|// in this experimental implementation, the crawler is pretty verbose
comment|// the SimpleLogger, however, is a FlyWeight logger which is buffered and
comment|// not thread safe by default
name|SimpleLogger
name|storeLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"store"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|visitedLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"URLVisitedFilter"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|scopeLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"URLScopeFilter"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|pathsLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"KnownPathsFilter"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|linksLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"links"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|SimpleLogger
name|lengthLog
init|=
operator|new
name|SimpleLogger
argument_list|(
literal|"length"
argument_list|,
comment|/* add date/time? */
literal|false
argument_list|)
decl_stmt|;
name|StoragePipeline
name|storage
init|=
operator|new
name|StoragePipeline
argument_list|()
decl_stmt|;
comment|// in the default configuration, the crawler will only save the document
comment|// information to store.log and the link information to links.log
comment|// The contents of the files are _not_ saved. If you set
comment|// "save in page files" to "true", they will be saved in "page files",
comment|// binary files each containing a set of documents. Here, the
comment|// maximum file size is ~50 MB (crawled files won't be split up into different
comment|// files). The logs/store.log file contains pointers to these files: a page
comment|// file number, the offset within that file, and the document's length
comment|// FIXME: default constructor for all storages + bean access methods
name|storage
operator|.
name|addDocStorage
argument_list|(
operator|new
name|LogStorage
argument_list|(
name|storeLog
argument_list|,
comment|/* save in page files? */
literal|false
argument_list|,
comment|/* page file prefix */
literal|"logs/pagefile"
argument_list|)
argument_list|)
expr_stmt|;
name|storage
operator|.
name|addLinkStorage
argument_list|(
operator|new
name|LinkLogStorage
argument_list|(
name|linksLog
argument_list|)
argument_list|)
expr_stmt|;
name|storage
operator|.
name|addLinkStorage
argument_list|(
name|messageHandler
argument_list|)
expr_stmt|;
comment|/*         // experimental Lucene storage. will slow the crawler down *a lot*         LuceneStorage luceneStorage = new LuceneStorage();         luceneStorage.setAnalyzer(new org.apache.lucene.analysis.de.GermanAnalyzer());         luceneStorage.setCreate(true); 	// FIXME: index name and path need to be configurable         luceneStorage.setIndexName("luceneIndex");         // the field names come from URLMessage.java and WebDocument.java. See         // LuceneStorage source for details         luceneStorage.setFieldInfo("url", LuceneStorage.INDEX | LuceneStorage.STORE);         luceneStorage.setFieldInfo("content", LuceneStorage.INDEX | LuceneStorage.STORE | LuceneStorage.TOKEN);         storage.addDocStorage(luceneStorage);         */
name|storage
operator|.
name|open
argument_list|()
expr_stmt|;
comment|//storage.addStorage(new JMSStorage(...));
comment|// create the filters and add them to the message queue
name|urlScopeFilter
operator|=
operator|new
name|URLScopeFilter
argument_list|(
name|scopeLog
argument_list|)
expr_stmt|;
comment|// dnsResolver = new DNSResolver();
name|hostManager
operator|=
operator|new
name|HostManager
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|hostResolver
operator|=
operator|new
name|HostResolver
argument_list|()
expr_stmt|;
name|hostResolver
operator|.
name|initFromFile
argument_list|(
name|hostResolverFile
argument_list|)
expr_stmt|;
name|hostManager
operator|.
name|setHostResolver
argument_list|(
name|hostResolver
argument_list|)
expr_stmt|;
comment|//        hostManager.addSynonym("www.fachsprachen.uni-muenchen.de", "www.fremdsprachen.uni-muenchen.de");
comment|//        hostManager.addSynonym("www.uni-muenchen.de", "www.lmu.de");
comment|//        hostManager.addSynonym("www.uni-muenchen.de", "uni-muenchen.de");
comment|//        hostManager.addSynonym("webinfo.uni-muenchen.de", "www.webinfo.uni-muenchen.de");
comment|//        hostManager.addSynonym("webinfo.uni-muenchen.de", "webinfo.campus.lmu.de");
comment|//        hostManager.addSynonym("www.s-a.uni-muenchen.de", "s-a.uni-muenchen.de");
name|reFilter
operator|=
operator|new
name|RobotExclusionFilter
argument_list|(
name|hostManager
argument_list|)
expr_stmt|;
name|fetcher
operator|=
operator|new
name|Fetcher
argument_list|(
name|nrThreads
argument_list|,
name|storage
argument_list|,
name|storage
argument_list|,
name|hostManager
argument_list|)
expr_stmt|;
comment|// prevent message box popups
name|HTTPConnection
operator|.
name|setDefaultAllowUserInteraction
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// prevent GZipped files from being decoded
name|HTTPConnection
operator|.
name|removeDefaultModule
argument_list|(
name|HTTPClient
operator|.
name|ContentEncodingModule
operator|.
name|class
argument_list|)
expr_stmt|;
name|urlVisitedFilter
operator|=
operator|new
name|URLVisitedFilter
argument_list|(
name|visitedLog
argument_list|,
literal|100000
argument_list|)
expr_stmt|;
comment|// initialize the threads
name|fetcher
operator|.
name|init
argument_list|()
expr_stmt|;
comment|// the thread monitor watches the thread pool.
name|monitor
operator|=
operator|new
name|ThreadMonitor
argument_list|(
name|urlLengthFilter
argument_list|,
name|urlVisitedFilter
argument_list|,
name|urlScopeFilter
argument_list|,
comment|/*dnsResolver,*/
name|reFilter
argument_list|,
name|messageHandler
argument_list|,
name|fetcher
operator|.
name|getThreadPool
argument_list|()
argument_list|,
name|hostManager
argument_list|,
literal|5000
comment|// wake up every 5 seconds
argument_list|)
expr_stmt|;
comment|// add all filters to the handler.
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlLengthFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlScopeFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|reFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|urlVisitedFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|knownPathsFilter
argument_list|)
expr_stmt|;
name|messageHandler
operator|.
name|addListener
argument_list|(
name|fetcher
argument_list|)
expr_stmt|;
comment|//uncomment this to enable HTTPClient logging
comment|/*         try         {             HTTPClient.Log.setLogWriter(new java.io.OutputStreamWriter(System.out) //new java.io.FileWriter("logs/HttpClient.log")             ,false);             HTTPClient.Log.setLogging(HTTPClient.Log.ALL, true);         }         catch (Exception e)         {             e.printStackTrace();         }         */
block|}
comment|/**      * Sets the RexString attribute of<code>UrlScopeFilter</code>.      *      * @param restrictTo the new RexString value      */
DECL|method|setRexString
specifier|public
name|void
name|setRexString
parameter_list|(
name|String
name|restrictTo
parameter_list|)
throws|throws
name|MalformedPatternException
block|{
name|urlScopeFilter
operator|.
name|setRexString
argument_list|(
name|restrictTo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Description of the Method      *      * @param url                                 Description of Parameter      * @param isFrame                             Description of the Parameter      * @exception java.net.MalformedURLException  Description of Exception      */
DECL|method|putURL
specifier|public
name|void
name|putURL
parameter_list|(
name|URL
name|url
parameter_list|,
name|boolean
name|isFrame
parameter_list|)
comment|//   throws java.net.MalformedURLException
block|{
try|try
block|{
name|messageHandler
operator|.
name|putMessage
argument_list|(
operator|new
name|URLMessage
argument_list|(
name|url
argument_list|,
literal|null
argument_list|,
name|isFrame
operator|==
literal|true
condition|?
name|URLMessage
operator|.
name|LINKTYPE_FRAME
else|:
name|URLMessage
operator|.
name|LINKTYPE_ANCHOR
argument_list|,
literal|null
argument_list|,
name|this
operator|.
name|hostResolver
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// FIXME: replace with logging
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Description of the Method      */
DECL|method|startMonitor
specifier|public
name|void
name|startMonitor
parameter_list|()
block|{
name|monitor
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/*      * the GUI is not working at this time. It was used in the very beginning, but      * synchronous updates turned out to slow down the program a lot, even if the      * GUI would be turned off. Thus, a lot      * of Observer messages where removed later. Nontheless, it's quite cool to see      * it working...      *      * @param f         Description of Parameter      * @param startURL  Description of Parameter      */
comment|/*     public void initGui(FetcherMain f, String startURL)     {         // if we're on a windows platform, make it look a bit more convenient         try         {             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());         }         catch (Exception e)         {             // dann halt nicht...         }         System.out.println("Init FetcherFrame");          FetcherSummaryFrame fetcherFrame;         fetcherFrame = new FetcherSummaryFrame();         fetcherFrame.setSize(640, 450);         fetcherFrame.setVisible(true);         FetcherGUIController guiController = new FetcherGUIController(f, fetcherFrame, startURL);     }         */
comment|/**      * The main program.      *      * @param args  The command line arguments      */
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|nrThreads
init|=
literal|10
decl_stmt|;
name|ArrayList
name|startURLs
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|String
name|restrictTo
init|=
literal|".*"
decl_stmt|;
name|boolean
name|gui
init|=
literal|false
decl_stmt|;
name|boolean
name|showInfo
init|=
literal|false
decl_stmt|;
name|String
name|hostResolverFile
init|=
literal|""
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"LARM - LANLab Retrieval Machine - Fetcher - V 1.00 - B.20020914"
argument_list|)
expr_stmt|;
comment|// FIXME: consider using Jakarta Commons' CLI package for command line parameters
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-start"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|String
name|arg
init|=
name|args
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
comment|// input is a file with one URL per line
name|String
name|fileName
init|=
name|arg
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"reading URL file "
operator|+
name|fileName
argument_list|)
expr_stmt|;
try|try
block|{
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|r
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|startURLs
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
name|line
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Malformed URL '"
operator|+
name|line
operator|+
literal|"' in line "
operator|+
operator|(
name|count
operator|+
literal|1
operator|)
operator|+
literal|" of file "
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"added "
operator|+
name|count
operator|+
literal|" URLs from "
operator|+
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Couldn't read '"
operator|+
name|fileName
operator|+
literal|"': "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"got URL "
operator|+
name|arg
argument_list|)
expr_stmt|;
try|try
block|{
name|startURLs
operator|.
name|add
argument_list|(
operator|new
name|URL
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Start-URL added: "
operator|+
name|arg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Malformed URL '"
operator|+
name|arg
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-restrictto"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|restrictTo
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Restricting URLs to "
operator|+
name|restrictTo
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-threads"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|nrThreads
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Threads set to "
operator|+
name|nrThreads
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-hostresolver"
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|hostResolverFile
operator|=
name|args
index|[
name|i
index|]
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"reading host resolver props from  '"
operator|+
name|hostResolverFile
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-gui"
argument_list|)
condition|)
block|{
name|gui
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"-?"
argument_list|)
condition|)
block|{
name|showInfo
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Unknown option: "
operator|+
name|args
index|[
name|i
index|]
operator|+
literal|"; use -? to get syntax"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|//URL.setURLStreamHandlerFactory(new HttpTimeoutFactory(500));
comment|// replaced by HTTPClient
name|FetcherMain
name|f
init|=
operator|new
name|FetcherMain
argument_list|(
name|nrThreads
argument_list|,
name|hostResolverFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|showInfo
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|hostResolverFile
argument_list|)
operator|||
operator|(
name|startURLs
operator|.
name|isEmpty
argument_list|()
operator|&&
name|gui
operator|==
literal|false
operator|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"The LARM crawler\n"
operator|+
literal|"\n"
operator|+
literal|"The LARM crawler is a fast parallel crawler, currently designed for\n"
operator|+
literal|"large intranets (up to a couple hundred hosts with some hundred thousand\n"
operator|+
literal|"documents). It is currently restricted by a relatively high memory overhead\n"
operator|+
literal|"per crawled host, and by a HashMap of already crawled URLs which is also held\n"
operator|+
literal|"in memory.\n"
operator|+
literal|"\n"
operator|+
literal|"Usage:   FetcherMain<-start<URL>|@<filename>>+ -restrictto<RegEx>\n"
operator|+
literal|"                    [-threads<nr=10>] [-hostresolver<filename>]\n"
operator|+
literal|"\n"
operator|+
literal|"Commands:\n"
operator|+
literal|"         -start specify one or more URLs to start with. You can as well specify a file"
operator|+
literal|"                that contains URLs, one each line\n"
operator|+
literal|"         -restrictto a Perl 5 regular expression each URL must match. It is run against the\n"
operator|+
literal|"                     _complete_ URL, including the http:// part\n"
operator|+
literal|"         -threads  the number of crawling threads. defaults to 10\n"
operator|+
literal|"         -hostresolver specify a file that contains rules for changing the host part of \n"
operator|+
literal|"                       a URL during the normalization process (experimental).\n"
operator|+
literal|"Caution: The<RegEx> is applied to the _normalized_ form of a URL.\n"
operator|+
literal|"         See URLNormalizer for details\n"
operator|+
literal|"Example:\n"
operator|+
literal|"    -start @urls1.txt -start @urls2.txt -start http://localhost/ "
operator|+
literal|"    -restrictto http://[^/]*\\.localhost/.* -threads 25\n"
operator|+
literal|"\n"
operator|+
literal|"The host resolver file may contain the following commands: \n"
operator|+
literal|"  startsWith(part1) = part2\n"
operator|+
literal|"      if host starts with part1, this part will be replaced by part2\n"
operator|+
literal|"   endsWith(part1) = part2\n"
operator|+
literal|"       if host ends with part1, this part will be replaced by part2. This is done after\n"
operator|+
literal|"       startsWith was processed\n"
operator|+
literal|"   synonym(host1) = host2\n"
operator|+
literal|"       the keywords startsWith, endsWith and synonym are case sensitive\n"
operator|+
literal|"       host1 will be replaced with host2. this is done _after_ startsWith and endsWith was \n"
operator|+
literal|"       processed. Due to a bug in BeanUtils, dots are not allowed in the keys (in parentheses)\n"
operator|+
literal|"       and have to be escaped with commas. To simplify, commas are also replaced in property \n"
operator|+
literal|"       values. So just use commas instead of dots. The resulting host names are only used for \n"
operator|+
literal|"       comparisons and do not have to be existing URLs (although the syntax has to be valid).\n"
operator|+
literal|"       However, the names will often be passed to java.net.URL which will try to make a DNS name\n"
operator|+
literal|"       resolution, which will time out if the server can't be found. \n"
operator|+
literal|"   Example:"
operator|+
literal|"     synonym(www1,host,com) = host,com\n"
operator|+
literal|"     startsWith(www,) = ,\n"
operator|+
literal|"     endsWith(host1,com) = host,com\n"
operator|+
literal|"The crawler will show a status message every 5 seconds, which is printed by ThreadMonitor.java\n"
operator|+
literal|"It will stop after the ThreadMonitor found the message queue and the crawling threads to be idle a \n"
operator|+
literal|"couple of times.\n"
operator|+
literal|"The crawled data will be saved within a logs/ directory. A cachingqueue/ directory is used for\n"
operator|+
literal|"temporary queues.\n"
operator|+
literal|"Note that this implementation is experimental, and that the command line options cover only a part \n"
operator|+
literal|"of the parameters. Much of the configuration can only be done by modifying FetcherMain.java\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|f
operator|.
name|setRexString
argument_list|(
name|restrictTo
argument_list|)
expr_stmt|;
if|if
condition|(
name|gui
condition|)
block|{
comment|// f.initGui(f, startURL);
comment|// the GUI is not longer supported
block|}
else|else
block|{
name|f
operator|.
name|startMonitor
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|it
init|=
name|startURLs
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|f
operator|.
name|putURL
argument_list|(
operator|(
name|URL
operator|)
name|it
operator|.
name|next
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|MalformedPatternException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Wrong RegEx syntax. Must be a valid PERL RE"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

