package IntegracionBackFront.backfront.Services.Cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;


@Service
public class CloudinaryService {

    //1. Constante para definir el tamaño permitifo a lad imagenes(5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    //2. Extensiones de archivo permitidas para subir a cloudinary
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png"};

    //3. Cliente de Cloudinary
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    //Subir imagenes a la raiz de cloudinary
    public String uploadImage(MultipartFile file) throws IOException {
        validateImage(file);
        Map<?, ?>uploadResult = cloudinary.uploader()
                .upload(file.getBytes(), ObjectUtils.asMap(
                        "resource_type", "auto",
                        "quality", "auto:good"
                ));
        return (String) uploadResult.get("secure_url");
    }
    public String uploadImage(MultipartFile  file, String folder) throws IOException{
        validateImage(file);
        String originalFileName = file.getOriginalFilename();
        String fileExtensions = originalFileName.substring(originalFileName.lastIndexOf(".")).toLowerCase();
        String uniqueFileName = "img_" + UUID.randomUUID() + fileExtensions;

        Map<String, Object> options = ObjectUtils.asMap(
                "folder", folder, //Carpeta de destino
                "public_id", uniqueFileName ,//nombre unico para el archivo
                "use_filename", false, //no usar el nombre original
                "unique_filename", false, //no genera nombre unico(ya lo hicimos)
                "override", false, // no sobreescribir archivos existentes
                "quality", "auto: good"

        );
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        return (String) uploadResult.get("secure_url");
    }

    private void validateImage(MultipartFile file) {
        //1. verificar si el archivo esta vacio
        if (file.isEmpty()) throw new IllegalArgumentException("El argumento no puede estar vacio");
        //2. verificar que el archivo no sobrepase el tamaño
        if (file.getSize() > MAX_FILE_SIZE)
            throw new IllegalArgumentException("El tamaño del archivo no puede exceder los 5MB");
        String originalFilename = file.getOriginalFilename();
        //3. validar el nombre del archivo
        if (originalFilename == null) throw new IllegalArgumentException("nombre del archivo no valido");
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension))
            throw new IllegalArgumentException("solo se permiten archivos .jpg, .jpeg o png");
        if (!file.getContentType().startsWith("image/"))
            throw new IllegalArgumentException("El archivo debe ser una imagen valida");
    }


        //Subir imagenes a carpetas de cloudinary
}
