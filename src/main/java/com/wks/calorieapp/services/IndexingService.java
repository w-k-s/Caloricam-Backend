package com.wks.calorieapp.services;

import com.wks.calorieapp.factories.ImagesDirectory;
import com.wks.calorieapp.factories.IndexesDirectory;
import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * CITATION
 * <p>
 * Mathias Lux (April 20, 2013), Creating an Index with LIRe
 * http://www.semanticmetadata.net/wiki/doku.php?id=lire:createindex
 * (retrieved on 5-Aug-2013)
 */
@Named
@ApplicationScoped
public class IndexingService {
    private static Logger logger = Logger.getLogger(IndexingService.class);
    private static Object lock = new Object();
    private File indexesDirectory;
    private File imagesDirectory;
    private DocumentBuilder documentBuilder;

    public IndexingService() {
        // Required by CDI to create a proxy class. The proxy class is created because of the Applicationscope
        // Possible Fix: https://stackoverflow.com/a/47540516
    }

    @Inject
    public IndexingService(
            DocumentBuilder builder,
            @IndexesDirectory File indexesDirectory,
            @ImagesDirectory File imagesDirectory
    ) {
        if (builder == null) throw new IllegalStateException("DocumentBuilder should not be null");
        if (indexesDirectory != null && !indexesDirectory.isDirectory())
            throw new IllegalArgumentException("The indexes File provided is not a directory.");
        this.documentBuilder = builder;
        this.indexesDirectory = indexesDirectory;
        this.imagesDirectory = imagesDirectory;
    }

    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    /**
     * @param imageFile  image to index
     * @return true if image was indexed successfully.
     * @throws IOException
     */
    public boolean indexImage(File imageFile) throws IOException {
        synchronized (lock) {
            if (imageFile != null && !imageFile.isFile())
                throw new IllegalArgumentException("The Image File provided is not a file.");


            ArrayList<String> images = new ArrayList<String>();
            images.add(imageFile.getAbsolutePath());
            return this.indexImages(images, indexesDirectory);
        }
    }

    /**
     * http://www.semanticmetadata.net/wiki/doku.php?id=lire:createindex Indexes
     * Images using the set document builder.
     *
     * @param imagesDir  directory where images to index are kept.
     * @param indexesDir directory where generated indexes are to be stored
     * @return true, if all images were indexed successfully
     * @throws IOException
     */
    public boolean indexImages(File imagesDir, File indexesDir) throws IOException {
        synchronized (lock) {
            if (imagesDir != null && !imagesDir.isDirectory())
                throw new IllegalArgumentException("The Image File provided is not a directory.");
            if (indexesDir != null && !indexesDir.isDirectory())
                throw new IllegalArgumentException("The indexes File provided is not a directory.");

            ArrayList<String> images = FileUtils.getAllImages(imagesDir, true);
            return this.indexImages(images, indexesDir);
        }
    }

    /**
     * Indexes all images using selected document builder.
     *
     * @param images     paths of images to index.
     * @param indexesDir directory where generated indexes are to be stored
     * @return true, if all images were indexed successfully
     * @throws IOException
     */
    private boolean indexImages(ArrayList<String> images, File indexesDir) throws IOException {
        boolean success = false;

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, new WhitespaceAnalyzer(Version.LUCENE_40));
        IndexWriter indexer = null;
        Directory indexesDirectory = null;
        try {
            // Initialise indexer with output location.
            indexesDirectory = FSDirectory.open(indexesDir);
            indexer = new IndexWriter(indexesDirectory, config);

            for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
                String imageFilePath = it.next();
                //load image
                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
                //create lucene document containing descriptor
                Document document = this.getDocumentBuilder().createDocument(img, imageFilePath);
                //index document
                indexer.addDocument(document);

            }

            success = true;

        } catch (FileNotFoundException e) {
            throw e;
        } catch (LockObtainFailedException e) {
            logger.error("Indexer. Lock ObtainFailedException. Will Unlock", e);
            if (indexesDirectory != null) {
                IndexWriter.unlock(indexesDirectory);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (indexer != null) {
                try {
                    indexer.close();
                } catch (IOException e) {
                    IndexWriter.unlock(indexesDirectory);
                }
            }

        }

        return success;

    }

    public boolean deleteIndexes() {
        return com.wks.calorieapp.utils.FileUtils.deleteFiles(getIndexFilesList());
    }

    public List<String> getIndexFilesList() {
        return com.wks.calorieapp.utils.FileUtils.getFilesInDir(indexesDirectory.getAbsolutePath(), new String[]{""});
    }

    public boolean reindex() throws IOException {
        return indexImages(imagesDirectory, indexesDirectory);
    }
}
