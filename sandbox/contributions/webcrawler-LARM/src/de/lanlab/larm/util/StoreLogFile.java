begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package

begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment

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
name|parser
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
name|fetcher
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

begin_comment
comment|/**  * Utility class for accessing page files through the store.log file.  * Works like an iterator  */
end_comment

begin_class
DECL|class|StoreLogFile
specifier|public
class|class
name|StoreLogFile
implements|implements
name|Iterator
block|{
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * @author Clemens Marschner      * @version 1.0      */
DECL|class|PageFileEntry
specifier|public
class|class
name|PageFileEntry
block|{
DECL|field|url
name|String
name|url
decl_stmt|;
DECL|field|pageFileNo
name|int
name|pageFileNo
decl_stmt|;
DECL|field|resultCode
name|int
name|resultCode
decl_stmt|;
DECL|field|mimeType
name|String
name|mimeType
decl_stmt|;
DECL|field|size
name|int
name|size
decl_stmt|;
DECL|field|title
name|String
name|title
decl_stmt|;
DECL|field|pageFileOffset
name|int
name|pageFileOffset
decl_stmt|;
DECL|field|pageFileDirectory
name|File
name|pageFileDirectory
decl_stmt|;
DECL|field|hasPageFileEntry
name|boolean
name|hasPageFileEntry
decl_stmt|;
DECL|field|isFrame
name|int
name|isFrame
decl_stmt|;
DECL|class|PageFileInputStream
class|class
name|PageFileInputStream
extends|extends
name|InputStream
block|{
DECL|field|pageFileIS
name|InputStream
name|pageFileIS
decl_stmt|;
DECL|field|offset
name|long
name|offset
decl_stmt|;
DECL|method|PageFileInputStream
specifier|public
name|PageFileInputStream
parameter_list|()
throws|throws
name|IOException
block|{
name|pageFileIS
operator|=
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|pageFileDirectory
argument_list|,
literal|"pagefile_"
operator|+
name|pageFileNo
operator|+
literal|".pfl"
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|=
literal|0
expr_stmt|;
name|pageFileIS
operator|.
name|skip
argument_list|(
name|pageFileOffset
argument_list|)
expr_stmt|;
block|}
DECL|method|available
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|pageFileIS
operator|.
name|available
argument_list|()
argument_list|,
call|(
name|int
call|)
argument_list|(
name|size
operator|-
name|offset
argument_list|)
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|pageFileIS
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|mark
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readLimit
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|markSupported
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|>=
name|size
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|c
init|=
name|pageFileIS
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|!=
operator|-
literal|1
condition|)
block|{
name|offset
operator|++
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
name|size
operator|-
name|offset
argument_list|)
argument_list|,
name|b
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|len
operator|=
name|pageFileIS
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|!=
operator|-
literal|1
condition|)
block|{
name|offset
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|maxLen
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
name|size
operator|-
name|offset
argument_list|)
argument_list|,
name|b
operator|.
name|length
argument_list|)
argument_list|,
name|maxLen
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|len
operator|=
name|pageFileIS
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|maxLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|!=
operator|-
literal|1
condition|)
block|{
name|offset
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|n
operator|=
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|size
operator|-
name|offset
argument_list|)
expr_stmt|;
name|n
operator|=
name|pageFileIS
operator|.
name|skip
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
name|offset
operator|+=
name|n
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
block|}
DECL|method|PageFileEntry
specifier|public
name|PageFileEntry
parameter_list|(
name|String
name|storeLogLine
parameter_list|,
name|File
name|pageFileDirectory
parameter_list|)
block|{
name|String
name|column
init|=
literal|null
decl_stmt|;
name|SimpleStringTokenizer
name|t
init|=
operator|new
name|SimpleStringTokenizer
argument_list|(
name|storeLogLine
argument_list|,
literal|'\t'
argument_list|)
decl_stmt|;
try|try
block|{
name|hasPageFileEntry
operator|=
literal|false
expr_stmt|;
name|t
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|url
operator|=
name|t
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|column
operator|=
literal|"isFrame"
expr_stmt|;
name|isFrame
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|nextToken
argument_list|()
expr_stmt|;
comment|// anchor
name|column
operator|=
literal|"resultCode"
expr_stmt|;
name|resultCode
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|mimeType
operator|=
name|t
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|column
operator|=
literal|"size"
expr_stmt|;
name|size
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|title
operator|=
name|t
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|column
operator|=
literal|"pageFileNo"
expr_stmt|;
name|pageFileNo
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|column
operator|=
literal|"pageFileOffset"
expr_stmt|;
name|pageFileOffset
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|pageFileDirectory
operator|=
name|pageFileDirectory
expr_stmt|;
name|hasPageFileEntry
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
comment|// possibly tab characters in title. ignore
block|{
comment|//System.out.println(e + " at " + url + " in column " + column);
block|}
block|}
DECL|method|getInputStream
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasPageFileEntry
condition|)
block|{
return|return
operator|new
name|PageFileInputStream
argument_list|()
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
block|}
DECL|field|reader
name|BufferedReader
name|reader
decl_stmt|;
DECL|field|isOpen
name|boolean
name|isOpen
init|=
literal|false
decl_stmt|;
DECL|field|storeLog
name|File
name|storeLog
decl_stmt|;
comment|/**      *      * @param storeLog location of store.log from LogStorage. pagefile_xy.pfl      * must be in the same directory      * @throws IOException      */
DECL|method|StoreLogFile
specifier|public
name|StoreLogFile
parameter_list|(
name|File
name|storeLog
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|storeLog
operator|=
name|storeLog
expr_stmt|;
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|FileReader
argument_list|(
name|storeLog
argument_list|)
argument_list|)
expr_stmt|;
name|isOpen
operator|=
literal|true
expr_stmt|;
comment|// unless exception
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
try|try
block|{
name|reader
operator|.
name|mark
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
name|reader
operator|.
name|readLine
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException occured"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @return a StoreLogFile.PageFileEntry with the current file      * @throws IOException      */
DECL|method|next
specifier|public
name|Object
name|next
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|PageFileEntry
argument_list|(
name|reader
operator|.
name|readLine
argument_list|()
argument_list|,
name|storeLog
operator|.
name|getParentFile
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"IOException occured"
argument_list|)
throw|;
block|}
block|}
comment|//    static SimpleLogger log;
comment|//    static PageFileEntry entry;
comment|//    static ArrayList foundURLs;
comment|//    static URL base;
comment|//    static URL contextUrl;
comment|//
comment|//    static void test1(StoreLogFile store) throws IOException
comment|//    {
comment|//        while(store.hasNext())
comment|//        {
comment|//            PageFileEntry entry = store.next();
comment|//            if(entry.mimeType.equals("text/plain")&& entry.hasPageFileEntry)
comment|//            {
comment|//                BufferedReader r = new BufferedReader(new InputStreamReader(entry.getInputStream()));
comment|//                String l;
comment|//                while((l = r.readLine()) != null)
comment|//                {
comment|//                    System.out.println(entry.url + ">> " + l);
comment|//                }
comment|//                r.close();
comment|//            }
comment|//            //System.out.println(entry.title);
comment|//        }
comment|//    }
comment|//    static void test2(StoreLogFile store) throws Exception
comment|//    {
comment|//        MessageHandler msgH = new MessageHandler();
comment|//        log = new SimpleLogger("errors.log");
comment|//        msgH.addListener(new URLVisitedFilter(log, 100000));
comment|//        final de.lanlab.larm.net.HostManager hm = new de.lanlab.larm.net.HostManager(1000);
comment|//        hm.setHostResolver(new HostResolver());
comment|//
comment|//        while(store.hasNext())
comment|//        {
comment|//            entry = store.next();
comment|//            foundURLs = new ArrayList();
comment|//            if(entry.mimeType.startsWith("text/html")&& entry.hasPageFileEntry)
comment|//            {
comment|//                Tokenizer t = new Tokenizer();
comment|//                base = new URL(entry.url);
comment|//                contextUrl = new URL(entry.url);
comment|//
comment|//                t.setLinkHandler(new LinkHandler()
comment|//                {
comment|//
comment|//                    public void handleLink(String link, String anchor, boolean isFrame)
comment|//                    {
comment|//                        try
comment|//                        {
comment|//                            // cut out Ref part
comment|//
comment|//
comment|//                            int refPart = link.indexOf("#");
comment|//                            //System.out.println(link);
comment|//                            if (refPart == 0)
comment|//                            {
comment|//                                return;
comment|//                            }
comment|//                            else if (refPart> 0)
comment|//                            {
comment|//                                link = link.substring(0, refPart);
comment|//                            }
comment|//
comment|//                            URL url = null;
comment|//                            if (link.startsWith("http:"))
comment|//                            {
comment|//                                // distinguish between absolute and relative URLs
comment|//
comment|//                                url = new URL(link);
comment|//                            }
comment|//                            else
comment|//                            {
comment|//                                // relative url
comment|//                                url = new URL(base, link);
comment|//                            }
comment|//
comment|//                            URLMessage urlMessage =  new URLMessage(url, contextUrl, isFrame ? URLMessage.LINKTYPE_FRAME : URLMessage.LINKTYPE_ANCHOR, anchor, hm.getHostResolver());
comment|//
comment|//                            String urlString = urlMessage.getURLString();
comment|//
comment|//                            foundURLs.add(urlMessage);
comment|//                            //messageHandler.putMessage(new actURLMessage(url)); // put them in the very end
comment|//                        }
comment|//                        catch (MalformedURLException e)
comment|//                        {
comment|//                            //log.log("malformed url: base:" + base + " -+- link:" + link);
comment|//                            log.log("warning: " + e.getClass().getName() + ": " + e.getMessage());
comment|//                        }
comment|//                        catch (Exception e)
comment|//                        {
comment|//                            log.log("warning: " + e.getClass().getName() + ": " + e.getMessage());
comment|//                            // e.printStackTrace();
comment|//                        }
comment|//
comment|//                    }
comment|//
comment|//
comment|//                    /**
comment|//                     * called when a BASE tag was found
comment|//                     *
comment|//                     * @param base  the HREF attribute
comment|//                     */
comment|//                    public void handleBase(String baseString)
comment|//                    {
comment|//                        try
comment|//                        {
comment|//                            base = new URL(baseString);
comment|//                        }
comment|//                        catch (MalformedURLException e)
comment|//                        {
comment|//                            log.log("warning: " + e.getClass().getName() + ": " + e.getMessage() + " while converting '" + base + "' to URL in document " + contextUrl);
comment|//                        }
comment|//                    }
comment|//
comment|//                    public void handleTitle(String value)
comment|//                    {}
comment|//
comment|//
comment|//                });
comment|//                t.parse(new BufferedReader(new InputStreamReader(entry.getInputStream())));
comment|//                msgH.putMessages(foundURLs);
comment|//            }
comment|//
comment|//        }
comment|//
comment|//    }
comment|//
comment|//    public static void main(String[] args) throws Exception
comment|//    {
comment|//        StoreLogFile store = new StoreLogFile(new File("c:/java/jakarta-lucene-sandbox/contributions/webcrawler-LARM/logs/store.log"));
comment|//        test2(store);
comment|//    }
block|}
end_class

end_unit

