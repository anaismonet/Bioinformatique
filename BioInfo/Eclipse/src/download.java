import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class download implements Runnable{
    Interface I;

    public download(Interface I) {
        this.I = I;
    }
    @Override
    public void run() {

        List<String> list_type = new ArrayList<>();

        //Interface I = new Interface();
        findInFile s = new findInFile();
        Dezip u = new Dezip();
        stat stat = new stat();
        Create_treestructure createstruct = new Create_treestructure();

        //arborescence
        File dossier = new File("Results");
        File eukaryotes = new File("./Results/Eukaryotes");
        File prokaryotes = new File("./Results/Prokaryotes");
        File plasmids = new File("./Results/Plasmids");
        File viruses = new File("./Results/Viruses");

        // Create files
        dossier.mkdir();
        eukaryotes.mkdir();
        prokaryotes.mkdir();
        plasmids.mkdir();
        viruses.mkdir();

        InputStream euka = null;
        InputStream summar = null;
        InputStream plasmi = null;
        InputStream viruse = null;
        InputStream prok = null;
        try {
            //Download of .txt we need to parse Genbank if it's not already done.
            File eukar = new File("Eukaryotes.txt");
            File summary = new File("summary.txt");
            File plasm = new File("Plasmids.txt");
            File virus= new File("Viruses.txt");
            File proka = new File("Prokaryotes.txt");

            if(!eukar.exists()) {
                euka = new URL("ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/eukaryotes.txt").openStream();
                System.out.println("Downloading Eukryote.txt...");
                Files.copy(euka, Paths.get("Eukaryotes.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            if(!proka.exists()) {
                prok = new URL("ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/prokaryotes.txt").openStream();
                System.out.println("Downloading Prokaryotes.txt...");
                Files.copy(prok, Paths.get("Prokaryotes.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            if(!summary.exists()) {
                summar = new URL("https://ftp.ncbi.nlm.nih.gov/genomes/refseq/assembly_summary_refseq.txt").openStream();
                System.out.println("Downloading summary.txt...");
                Files.copy(summar, Paths.get("summary.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            if(!plasm.exists()) {
                plasmi = new URL("ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/plasmids.txt").openStream();
                System.out.println("Downloading Plasmids.txt...");
                Files.copy(plasmi, Paths.get("Plasmids.txt"), StandardCopyOption.REPLACE_EXISTING);
            }
            if(!virus.exists()) {
                viruse = new URL("ftp://ftp.ncbi.nlm.nih.gov/genomes/GENOME_REPORTS/viruses.txt").openStream();
                System.out.println("Downloading Viruses.txt...");
                Files.copy(viruse, Paths.get("Viruses.txt"), StandardCopyOption.REPLACE_EXISTING);
            }

            //list_type.add("Eukaryotes");
            list_type.add("Plasmids");
            list_type.add("Prokaryotes");
            list_type.add("Viruses");

        } catch (IOException e) {
            e.printStackTrace();
        }

        //InputStream fna = null;
        String path;
        List<String> list_Name = new ArrayList<>();
        List<String> list_Group = new ArrayList<>();
        List<String> list_path = new ArrayList<>();
        List<String> list_SubGroup = new ArrayList<>();
        // advancement
        int avancement = 0;
        // for all type (Eukaryote ...) but only work for Eukaryote
        for(int p = 0; p<list_type.size();p++) {
            try {

                //update of the progressbar (doesn't work)
                I.setPercentage(avancement);
                I.progressBar.setVisible(true);
                System.out.println("file treated "+Paths.get(list_type.get(p)+".txt"));
                //finding NC in .txt files corresponding to the files we need to download
                int file_type = -1;
                switch (list_type.get(p)) {
                    case "Eukaryotes":
                        file_type = 0;
                        break;
                    case "Prokaryotes":
                        file_type = 1;
                        break;
                    case "Plasmids":
                        file_type = 2;
                        break;
                    case "Viruses":
                        file_type = 3;
                        break;
                }
                if (file_type==-1){
                    System.exit(0);
                }

                List<List> list_charac = s.findInFile(Paths.get(list_type.get(p)+".txt"), "NC_", 0,file_type);

                list_Name = list_charac.get(0);
                list_Group = list_charac.get(1);
                list_SubGroup = list_charac.get(2);

                //list_GCA = list_charac.get(3);
                System.out.println(Arrays.toString(list_Name.toArray()));
                System.out.println(Arrays.toString(list_Group.toArray()));
                System.out.println(Arrays.toString(list_SubGroup.toArray()));
                //System.out.println(Arrays.toString(list_GCA.toArray()));
                String[] pathName1;
                String[] pathName;
                for (String a : list_Name) {
                    //looking for the path ot downloads in the summary.txt which contains all of informations wih a given name
                    path = s.findPathInFile(Paths.get("summary.txt"), a, 0);
                    if (path.length() > 5) {
                        pathName1 = path.split("\t", 2);
                        pathName = pathName1[0].split("/", 10);
                        list_path.add(pathName1[0] + "/" + pathName[9] + "_genomic.gbff.gz");
                    }
                }
                System.out.println(Arrays.toString(list_path.toArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int r = 0; r < list_path.size(); r++) {
                avancement = p + (int)((r/list_path.size())/list_type.size());
                //create folder
                String link = "./results/" +list_type.get(p) + "/" + list_Group.get(r) + "/" + list_SubGroup.get(r);
                createstruct.create_folder(link);
                // create xls file
                File exist = new File(link + "/" + list_Name.get(r) + ".xls");
                System.out.println("create folder : " +exist);
                if (exist.exists()) {
                    continue;
                }

                I.AddMsgLog("treating " + list_Name.get(r));
                /*
                try {
                    fna = new URL(list_path.get(r)).openStream();
                    System.out.println(list_path.get(r));


                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                String filenameDNA = "DownloadDone.txt";
                try {
                    System.out.println(list_path.get(r));
                    FileUtils.copyURLToFile(
                            new URL(list_path.get(r)),
                            new File(Paths.get("DNA_file.gbff.gz").toString()));
                    //Files.copy(fna, Paths.get("DNA_file.gbff.gz"), StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                u.gunzipIt("DNA_file.gbff.gz", filenameDNA);
                File file_to_delete = new File("DNA_file.gbff.gz");
                file_to_delete.delete();

                String gene = null;
                Path filePath = Paths.get(filenameDNA);
                List<List> list_by_NC = new ArrayList<>();
                List<String> list_seq = new ArrayList<>();
                List<List> list_seq_cds_byNC = new ArrayList<>();
                List<List> list_seqCDS_invCDS_NC = new ArrayList<>();
                List<Integer> list_invCDS = new ArrayList<>();
                String name_sequence = null;
                try {
                    //Read NC gene
                    list_by_NC = s.getSeqNC(filePath, 0);
                    list_seq = s.get_seq(filePath, list_by_NC, 0);
                    list_seqCDS_invCDS_NC = s.create_seq_cds(list_seq, list_by_NC);
                    list_seq_cds_byNC = list_seqCDS_invCDS_NC.get(0);
                    list_invCDS = list_seqCDS_invCDS_NC.get(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                WritableWorkbook xls_file = s.create_xls_file(link + "/" + list_Name.get(r) + ".xls");
                boolean something_to_write = false;
                int nb_inv_CDS;
                int nb_ValidCDS_seq = 0;
                int nb_InvCDS_seq = 0;
                int nb_seq = 0;
                for (int sequence = 0; sequence < list_seq.size(); sequence += 1) {
                    // get sequence
                    try {
                        name_sequence = s.underscoreName((String) list_by_NC.get(sequence).get(0),
                                                 2, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    name_sequence = "DNA" + name_sequence;
                    nb_inv_CDS = list_invCDS.get(sequence);

                    // Tableau
                    int occ0[] = new int[64];
                    int occ1[] = new int[64];
                    int occ2[] = new int[64];

                    stat.init_tab(occ0, 64);
                    stat.init_tab(occ1, 64);
                    stat.init_tab(occ2, 64);

                    int nb_trinucl_total = 0;
                    String list[] = new String[64];

                    // list trinucléotide
                    char base[] = {'A', 'C', 'G', 'T'};
                    StringBuffer trinucl = new StringBuffer("---");

                    for (int i = 0; i < 4; i++) {
                        trinucl.setCharAt(0, base[i]);
                        for (int j = 0; j < 4; j++) {
                            trinucl.setCharAt(1, base[j]);
                            for (int k = 0; k < 4; k++) {
                                trinucl.setCharAt(2, base[k]);
                                list[k + 4 * j + 16 * i] = trinucl.toString();
                            }
                        }
                    }

                    for (int z = 0; z < list_seq_cds_byNC.get(sequence).size(); z++) {
                        gene = (String) list_seq_cds_byNC.get(sequence).get(z);
                        int taille_gene = gene.length();
                        // Calcul d'occurence
                        nb_trinucl_total += taille_gene / 3;
                        int nb_trinucl = taille_gene / 3;
                        if (stat.occurence(list, occ0, occ1, occ2, gene, nb_trinucl) == -1) {
                            continue;
                        }
                    }

                    try {
                        xls_file = s.write_in_xlsFile(xls_file, name_sequence, list, occ0, occ1, occ2, nb_trinucl_total,
                                                      nb_inv_CDS, list_by_NC.get(sequence).size() - 1);
                        nb_InvCDS_seq = nb_InvCDS_seq + nb_inv_CDS;
                        nb_ValidCDS_seq = nb_ValidCDS_seq + list_by_NC.get(sequence).size() - 1;
                        nb_seq++;
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                    something_to_write = true;
                }
                xls_file = s.write_xlsFile_GI(xls_file, list_Name.get(r), nb_ValidCDS_seq,nb_InvCDS_seq, nb_seq);
                if (xls_file != null) {
                    try {
                        if (something_to_write) {
                            xls_file.write();
                        }
                        xls_file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }
                file_to_delete = new File(filenameDNA);
                file_to_delete.delete();
                I.AddMsgLog("done with " + list_Name.get(r));
                I.setPercentage((int) (avancement/list_type.size())*100);
                I.progressBar.setVisible(true);
                I.listRoot();
            }
        }
    }
}